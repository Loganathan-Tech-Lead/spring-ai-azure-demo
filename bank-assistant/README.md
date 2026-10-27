# Bank Assistant

A two-service demo built with Spring Boot and Spring AI on Azure OpenAI. A customer asks questions in plain language ("Show my last 3 transactions on ACC-1001"), and the AI assistant answers by calling a separate account service through tools.

## Architecture

```
client --> ai-assistant-service (8083) --> account-service (8081)
                    |
                    v
             Azure OpenAI (chat model)
```

| Module | Port | Purpose |
|---|---|---|
| `account-service` | 8081 | REST API for accounts and transactions. Uses H2 with seed data and checks that an account belongs to the calling customer. |
| `ai-assistant-service` | 8083 | Chat endpoint. Uses Spring AI tools (`listAccounts`, `getBalance`, `getTransactions`) that call `account-service`. |

The customer id is sent in the `X-Customer-Id` header and passed to the tools through `ToolContext`, so the model never sees it and cannot change it.

## Project layout

```
bank-assistant/
├── pom.xml                  # parent pom
├── account-service/         # pom.xml, Dockerfile, src/
├── ai-assistant-service/    # pom.xml, Dockerfile, src/
└── README.md
```

The Kubernetes manifests for these services are in `k8s/account-service.yaml` and `k8s/ai-assistant.yaml` at the repository root.

## Prerequisites

- Java 21 and Maven
- An Azure OpenAI resource with a chat model deployment
- Docker (to build images) and the Azure CLI
- `kubectl` and an AKS cluster (for the Kubernetes steps)

## Configuration

The assistant reads its settings from environment variables. Never commit real values.

| Variable | Description |
|---|---|
| `AZURE_OPENAI_API_KEY` | API key of the Azure OpenAI resource |
| `AZURE_OPENAI_ENDPOINT` | Endpoint, for example `https://<resource>.openai.azure.com/` |
| `AZURE_OPENAI_DEPLOYMENT` | Chat deployment name (default `gpt-4.1-mini`) |
| `ACCOUNT_SERVICE_URL` | URL of the account service (default `http://localhost:8081`) |

## Run locally

Open three terminals.

1. Start the account service:

   ```bash
   cd bank-assistant/account-service
   mvn spring-boot:run
   ```

2. Start the assistant. Export the variables in the same terminal first:

   ```bash
   export AZURE_OPENAI_ENDPOINT='https://<resource>.openai.azure.com/'
   read -s -p "Key: " AZURE_OPENAI_API_KEY; export AZURE_OPENAI_API_KEY

   cd bank-assistant/ai-assistant-service
   mvn spring-boot:run
   ```

3. Send a request:

   ```bash
   curl -i -X POST http://localhost:8083/api/chat \
     -H "X-Customer-Id: C001" \
     -H "Content-Type: application/json" \
     -d '{"message":"Show my last 3 transactions on ACC-1001"}'
   ```

To check `account-service` on its own:

```bash
curl -H "X-Customer-Id: C001" "http://localhost:8081/api/accounts/ACC-1001/transactions?limit=3"
```

## Build and push images

`az acr build` is blocked on some Azure for Students subscriptions, so build with Docker and push. Run from the `bank-assistant` folder, because both Dockerfiles use it as the build context.

```bash
az acr login -n springaiacr2026

docker build -t springaiacr2026.azurecr.io/account-service:v1 -f account-service/Dockerfile .
docker push springaiacr2026.azurecr.io/account-service:v1

docker build -t springaiacr2026.azurecr.io/ai-assistant-service:v1 -f ai-assistant-service/Dockerfile .
docker push springaiacr2026.azurecr.io/ai-assistant-service:v1
```

## Deploy to AKS

Start the cluster and connect:

```bash
az aks start -g spring-ai-rg -n spring-ai-aks
az aks get-credentials -g spring-ai-rg -n spring-ai-aks --overwrite-existing
kubectl get nodes
```

The assistant reads the Azure key and endpoint from a Kubernetes Secret named `azure-openai-secret` with the keys `api-key` and `endpoint`. Create it from the live Azure values:

```bash
EP=$(az cognitiveservices account show -g logan-spring-rg -n logan-ai-service --query properties.endpoint -o tsv)
KEY=$(az cognitiveservices account keys list -g logan-spring-rg -n logan-ai-service --query key1 -o tsv)

kubectl create secret generic azure-openai-secret \
  --from-literal=endpoint="$EP" \
  --from-literal=api-key="$KEY"
```

Apply only the two bank-assistant manifests (not the whole `k8s/` folder, which also holds the older demo):

```bash
kubectl apply -f k8s/account-service.yaml
kubectl apply -f k8s/ai-assistant.yaml
kubectl get pods -w
```

Both pods should reach `1/1`. Inside the cluster the assistant calls `http://account-service:8081`.

## Test on AKS

Both Services are `ClusterIP`, so there is no public IP. Use a port-forward:

```bash
kubectl port-forward svc/ai-assistant-service 8083:8083 &
curl -s -X POST http://localhost:8083/api/chat \
  -H "X-Customer-Id: C001" \
  -H "Content-Type: application/json" \
  -d '{"message":"Show my last 3 transactions on ACC-1001"}'
```

## Stop to save credit

```bash
kill %1                                          # stop the port-forward
az aks stop -g spring-ai-rg -n spring-ai-aks
```

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| HTTP 500 from `/api/chat`, `401` in the assistant log | `AZURE_OPENAI_API_KEY` is missing or wrong, or the endpoint does not match the key |
| Assistant says it has no access to transactions | The chat call failed before the tool ran. Read the assistant log first |
| `kubectl`: `no such host` | The AKS cluster is stopped. Run `az aks start` |
| `ImagePullBackOff` | Wrong image name or tag, or the `acr-pull` secret is missing |
| Pod `0/1` for the first minute | Normal. The readiness probe waits before its first check |
| Pod `Pending` | The node is short on CPU or memory. Check `kubectl describe pod` |
| `TasksOperationsNotAllowed` from `az acr build` | ACR Tasks is blocked on the subscription. Build with Docker instead |

## Security notes

- Keys and passwords live in environment variables and Kubernetes Secrets, not in Git.
- `X-Customer-Id` is trusted as sent. Put real authentication in front of the assistant before exposing it publicly.
- `target/` folders should be listed in `.gitignore`.

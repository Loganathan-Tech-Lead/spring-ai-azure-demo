FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp package \
    && cp target/*.jar app.jar

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S -g 10001 app \
    && adduser -S -u 10001 -G app app

COPY --from=build --chown=app:app /workspace/app.jar ./app.jar

USER app:app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
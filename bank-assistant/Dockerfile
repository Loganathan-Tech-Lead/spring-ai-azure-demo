FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY account-service/pom.xml account-service/
COPY loan-service/pom.xml loan-service/
COPY ai-assistant-service/pom.xml ai-assistant-service/
RUN mvn -q -B -pl loan-service -am dependency:go-offline
COPY loan-service/src loan-service/src
RUN mvn -q -B -pl loan-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/loan-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]

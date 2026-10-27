FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY loan-service/pom.xml loan-service/
COPY account-service/pom.xml account-service/
COPY ai-assistant-service/pom.xml ai-assistant-service/
COPY loan-service/src loan-service/src
RUN mvn -q -pl loan-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/loan-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]

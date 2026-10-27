FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY account-service/pom.xml account-service/
COPY ai-assistant-service/pom.xml ai-assistant-service/
COPY account-service/src account-service/src
RUN mvn -q -pl account-service -am -DskipTests package

FROM eclipse-temurin:21-jre
RUN useradd -r -u 1001 app
USER 1001
WORKDIR /app
COPY --from=build /src/account-service/target/*.jar app.jar
EXPOSE 8081
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70"
ENTRYPOINT ["java","-jar","app.jar"]

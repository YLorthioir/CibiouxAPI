# Stage 1: Build the Maven project and create the JAR file
FROM maven:3.6.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -Ptest

# Stage 2: Create the final image with only the JAR file
FROM openjdk:17
ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} cibiouxREST.jar
ENTRYPOINT ["java", "-jar", "/cibiouxREST.jar"]
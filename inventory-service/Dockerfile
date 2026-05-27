FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle :inventory-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/inventory-service/build/libs/inventory-service-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

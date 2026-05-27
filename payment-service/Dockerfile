# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
COPY common ./common
COPY payment-service ./payment-service

RUN chmod +x gradlew && ./gradlew :payment-service:bootJar --no-daemon -x test

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/payment-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

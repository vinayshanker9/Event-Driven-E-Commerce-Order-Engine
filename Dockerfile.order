# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace

# Copy Gradle wrapper + root build files first (layer cache)
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# Copy modules needed for order-service
COPY common ./common
COPY order-service ./order-service

# Build the fat JAR (skip tests for faster CI builds)
RUN gradle :order-service:bootJar --no-daemon -x test

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/order-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

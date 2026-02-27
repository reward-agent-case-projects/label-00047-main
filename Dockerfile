# Multi-stage build setup
# Stage 1: Build the application
# Use a Gradle image that supports multiple architectures (ARM/AMD64)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Optimization: Copy dependency definitions first to utilize Docker cache layer
# This prevents re-downloading dependencies if only source code changes
COPY build.gradle.kts settings.gradle.kts* ./
# Pre-download dependencies (ignore errors if source is missing, goal is caching jars)
RUN gradle dependencies --no-daemon > /dev/null 2>&1 || true

# Copy source code after dependencies are cached
COPY src ./src

# Skip tests to speed up build, assuming tests pass in CI/CD
# --no-daemon helps in CI/Docker environments
RUN gradle bootJar --no-daemon -x test

# Stage 2: Create the runtime image
# Use a lightweight JRE image that supports multiple architectures
FROM eclipse-temurin:17-jre-jammy AS runner
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (optional for console apps, but good practice if we add web later)
# Since this is a console app, we actually need to attach to the container's TTY to interact.
# We don't necessarily expose a port for HTTP.

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]

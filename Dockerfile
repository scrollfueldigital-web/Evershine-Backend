# --- Stage 1: Build JAR ---
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy Maven files first for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Fix Linux execute permission for Maven Wrapper
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build Spring Boot JAR
RUN ./mvnw clean package -DskipTests


# --- Stage 2: Runtime Image ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

USER appuser

# Copy generated JAR
COPY --from=builder /app/target/*.jar app.jar

# Documentation/default port
EXPOSE 8080

# Start application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
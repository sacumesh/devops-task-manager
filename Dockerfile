# -------------------------
# Stage 1: Build stage
# -------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Reduce Maven noise and speed up builds
ENV MAVEN_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Djava.awt.headless=true"
ENV MAVEN_CLI_OPTS="-B -DskipTests -Dhttp.keepAlive=true -Dmaven.wagon.http.pool=true -Dmaven.wagon.http.retryCount=3 -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"

# Copy only files needed to resolve dependencies (caching)
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./

# Pre-fetch dependencies using BuildKit cache (best for iterative builds)
RUN --mount=type=cache,target=/root/.m2 \
  ./mvnw ${MAVEN_CLI_OPTS} -T 1C dependency:go-offline

# Copy source and build
COPY src/ ./src/

# Optional tests toggle
ARG RUN_TESTS=false
RUN --mount=type=cache,target=/root/.m2 \
  if [ "$RUN_TESTS" = "true" ]; then \
    ./mvnw -B -T 1C clean package; \
  else \
    ./mvnw -B -T 1C clean package -DskipTests; \
  fi

# -------------------------
# Stage 2: Runtime stage
# -------------------------
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Minimal runtime metadata
LABEL org.opencontainers.image.title="devops-task-manager" \
    org.opencontainers.image.description="Task manager service" \
    org.opencontainers.image.source="https://example.com/repo" \
    org.opencontainers.image.vendor="YourOrg" \
    org.opencontainers.image.licenses="Apache-2.0"

# Create non-root user early
ARG APP_UID=10001
ARG APP_GID=10001
RUN groupadd -r -g ${APP_GID} appgroup && \
  useradd  -r -u ${APP_UID} -g appgroup -M -d /app -s /usr/sbin/nologin appuser

# Copy built artifact
# If multiple jars exist, prefer the one with 'jar-with-dependencies' or finalName via ARG
ARG JAR_PATH=/app/target
COPY --from=build ${JAR_PATH}/*.jar /app/app.jar

# Tighten permissions
RUN chown -R appuser:appgroup /app && \
  chmod 0755 /app && chmod 0644 /app/app.jar

USER appuser:appgroup

# Use a dedicated port env for flexibility
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

# Use exec form so signals are forwarded correctly
ENTRYPOINT ["java","-jar","/app/app.jar"]
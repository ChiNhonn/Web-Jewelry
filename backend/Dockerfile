# Stage 1: Runtime Image using Lightweight Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="AURA Luxury Jewelry Tech Team <tech@aurajewelry.com>"
LABEL description="Production Docker image for AURA Fine Jewelry Backend Service"

WORKDIR /app

# Create a non-privileged system user for enterprise security compliance
RUN addgroup -S aura && adduser -S aura -G aura

# Copy pre-packaged executable JAR
COPY target/jewelry-store-backend-1.0.0-SNAPSHOT.jar app.jar

# Set file permissions
RUN chown -R aura:aura /app

USER aura

# JVM flags optimized for container environments (G1GC, String Deduplication, memory limits)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

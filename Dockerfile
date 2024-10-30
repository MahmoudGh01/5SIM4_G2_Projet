# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-alpine

# Expose port 8082
EXPOSE 8082

# Set environment variables for Nexus credentials and URL
ARG NEXUS_USER
ARG NEXUS_PASS
ARG NEXUS_URL

# Download the JAR from Nexus
RUN apk add --no-cache curl && \
    curl -u $NEXUS_USER:$NEXUS_PASS -o gestion-station-ski-1.0.jar $NEXUS_URL

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/gestion-station-ski-1.0.jar"]
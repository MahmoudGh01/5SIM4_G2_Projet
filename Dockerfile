# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-alpine

# Expose port 8082
EXPOSE 8082



# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/gestion-station-ski-1.0.jar"]
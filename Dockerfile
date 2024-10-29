# Utilisez l'image OpenJDK 17 depuis DockerHub
FROM openjdk:17-alpine

# Exposez le port sur lequel votre application Spring Boot sera accessible
EXPOSE 8089

# Ajoutez le livrable (fichier JAR) généré par Maven dans l'image Docker
ADD target/gestion-station-ski-1.0.jar gestion-station-ski-1.0.jar

# Définissez le point d'entrée de l'application Spring Boot
ENTRYPOINT ["java","-jar","/gestion-station-ski-1.0.jar"]

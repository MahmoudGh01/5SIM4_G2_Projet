FROM openjdk:17-jdk-alpine
EXPOSE 8082
ARG NEXUS_USER
ARG NEXUS_PASS
ARG NEXUS_URL
# Update mirror and install curl, then download the JAR from Nexus
RUN sed -i 's|dl-cdn.alpinelinux.org|mirror.sjtu.edu.cn|g' /etc/apk/repositories && \
    apk update && \
    apk add --no-cache curl && \
    curl -u "$NEXUS_USER:$NEXUS_PASS" -o gestion-station-ski-1.0.jar "$NEXUS_URL"
ENTRYPOINT ["java","-jar","/gestion-station-ski-1.0.jar"]
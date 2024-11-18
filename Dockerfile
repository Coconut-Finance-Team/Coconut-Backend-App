# Backend Dockerfile
FROM openjdk:17-slim
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EXPOSE 8080
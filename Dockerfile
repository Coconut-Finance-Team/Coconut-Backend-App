FROM openjdk:17-slim
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
COPY build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

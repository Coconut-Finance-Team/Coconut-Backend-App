FROM openjdk:17-slim

WORKDIR /app

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 디렉토리에 끝에 / 추가
COPY build/libs/*.jar /app/

# jar 파일 이름을 app.jar로 변경
RUN mv /app/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

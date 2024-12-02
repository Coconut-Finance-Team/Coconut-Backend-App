FROM openjdk:17-slim

WORKDIR /app

# JVM 튜닝 옵션을 환경변수로 설정
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 애플리케이션 파일 복사
COPY build/libs/*.jar app.jar

# 포트 설정
EXPOSE 8080

# 환경변수를 포함한 실행 명령
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

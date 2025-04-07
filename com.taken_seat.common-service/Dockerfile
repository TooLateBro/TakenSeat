# Dockerfile

# JDK 17 기반의 경량화된 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉터리 설정
WORKDIR /app

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# JAP 파일 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 실행 명령어
ENTRYPOINT ["java", "-jar" ,"app.jar"]
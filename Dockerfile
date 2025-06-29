# Dockerfile

# jdk17 Image Start
FROM openjdk:17

# 인자 설정 - JAR_File
#ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
#COPY ${JAR_FILE} app.jar

# 인자 설정 부분과 jar 파일 복제 부분 합쳐서 진행해도 무방
COPY build/libs/*.jar app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--server.address=0.0.0.0"]

# 컨테이너의 기본 시간대를 'Asia/Seoul'로 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
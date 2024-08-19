FROM openjdk:17

RUN sudo ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
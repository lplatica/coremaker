FROM openjdk:17-jdk-slim-buster
ARG JAR_FILE=target/demo.jar
COPY ${JAR_FILE} demo.jar
ENTRYPOINT ["java","-jar","/demo.jar"]
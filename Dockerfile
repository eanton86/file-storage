FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/file-storage-1.0.0.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]


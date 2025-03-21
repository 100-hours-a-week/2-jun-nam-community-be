FROM openjdk:17-jdk-slim

# build-args (이미지 내에 저장되지 않음)
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD

WORKDIR /app
COPY build/libs/community-be-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

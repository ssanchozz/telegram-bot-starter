# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY domain/ domain
COPY telegram-api/ telegram-api

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=\"-Xmx512m\""]
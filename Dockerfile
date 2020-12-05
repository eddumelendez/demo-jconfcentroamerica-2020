FROM openjdk:11.0.9.1-jdk AS builder

COPY src /usr/app/src
COPY pom.xml /usr/app
COPY .mvn /usr/app/.mvn
COPY mvnw /usr/app

WORKDIR /usr/app

RUN ./mvnw clean package -DskipTests

FROM openjdk:11.0.9.1-jre

COPY --from=builder /usr/app/target/*.jar app.jar

ENTRYPOINT java -jar app.jar

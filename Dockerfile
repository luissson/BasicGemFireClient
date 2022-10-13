# syntax=docker/dockerfile:1
FROM maven:3.3-jdk-8 AS build

WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN mvn clean compile assembly:single

FROM bellsoft/liberica-openjre-alpine:8u345-1 AS run

COPY --from=build /build/target/gemfireclient-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/gemfireclient.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/gemfireclient.jar"]

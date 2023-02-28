# syntax=docker/dockerfile:1
FROM bellsoft/liberica-openjre-alpine:8u345-1 AS run
WORKDIR /build
COPY target/gemfireclient-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/gemfireclient.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/gemfireclient.jar"]

#Stage Build
FROM maven:3.8.3-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#Package
FROM openjdk:17-jdk-alpine
LABEL key="EverSync | rajandeepvij@gmail.com"
RUN mkdir -p /site/uploads
COPY --from=build /home/app/target/eversync-s3filehandler.jar /usr/local/eversync-s3filehandler.jar
ENTRYPOINT ["java","-jar","/usr/local/eversync-s3filehandler.jar"]
FROM openjdk:8-jdk-alpine
MAINTAINER https://github.com/sandeepvedavyas
VOLUME /tmp
EXPOSE 8080
ADD target/backendtask.jar backendtask.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/backendtask.jar"]
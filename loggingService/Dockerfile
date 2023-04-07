#https://docs.docker.com/engine/reference/builder/
# get java image from docker hub
FROM amazoncorretto:17
# set environment variable profile with default value dev
ENV PROFILE dev
# current work directory 
WORKDIR /app/logging_service
# copy generated jar
COPY ./target/*.jar logging_service.jar
# copy certificates
COPY ./src/main/resources/microservices.p12 microservices.p12
# start jar with specific profile from compose
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "logging_service.jar"]

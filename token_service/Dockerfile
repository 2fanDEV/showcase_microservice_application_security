# https://docs.docker.com/engine/reference/builder/#entrypoint
# get java image from docker hub
FROM amazoncorretto:17
# create environment variable with default value dev
ENV PROFILE dev
# current work directory in container
WORKDIR /app/token_service
# copy generated jar 
COPY ./target/*.jar token_service.jar
# copy certificates
COPY ./src/main/resources/microservices.p12 microservices.p12
# start jar with profile from compose
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "token_service.jar"]

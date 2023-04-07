# https://docs.docker.com/engine/reference/builder/
## image to be used is amazoncorreto which has Java 17 integrated
FROM amazoncorretto:17
# set a environment variable with dev as default value
ENV PROFILE dev
#initialize the working directory inside the container
WORKDIR /app/user_service
#copy the jar
COPY ./target/*.jar user_service.jar
#copy the self signed certificate
COPY ./src/main/resources/microservices.p12 microservices.p12
#start the jar with a profile that is passed through the docker compose
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "user_service.jar"]


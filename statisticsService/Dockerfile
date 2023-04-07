# https://docs.docker.com/engine/reference/builder/
# get java image from docker hub
FROM amazoncorretto:17
# create environment variable with default value dev
ENV PROFILE dev
# set working directory inside of container
WORKDIR /app/statistics_service
# copy generated jar 
COPY ./target/*.jar statistics_service.jar
# copy certificates
COPY ./src/main/resources/microservices.p12 microservices.p12
# start jar with specific profile from compose
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "statistics_service.jar"]


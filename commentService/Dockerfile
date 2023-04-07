# https://docs.docker.com/engine/reference/builder/
# select java image from docker hub
FROM amazoncorretto:17
# create environment variable with default value dev
ENV PROFILE dev
# the work directory the container is in
WORKDIR /app/comment_service
# copy the generated jar 
COPY ./target/*.jar comment_service.jar
#copy the certificates
COPY ./src/main/resources/microservices.p12 microservices.p12
#start the application with specific profile
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "comment_service.jar"]

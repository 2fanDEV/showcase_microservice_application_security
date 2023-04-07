# https://docs.docker.com/engine/reference/builder/
# select java image from hub
FROM amazoncorretto:17
# create environment variable profile with default value dev
ENV PROFILE dev
# current work directory inside of container
WORKDIR /app/blog_service
# copy the generated jar
COPY ./target/*.jar blog_service.jar
# copy the generated certificate
COPY ./src/main/resources/microservices.p12 microservices.p12
# start the jar with specific profile from the docker compose
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "blog_service.jar"]

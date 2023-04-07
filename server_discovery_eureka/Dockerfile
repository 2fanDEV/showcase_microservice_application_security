# https://docs.docker.com/engine/reference/builder/
# get an image with java 17
FROM amazoncorretto:17
# create an environment variable called PROFILE
# that holds the default value dev
ENV PROFILE dev
# the directory inside the container which is to be used
WORKDIR /app/service_discovery_eureka
#copy the created .jar to current directory
COPY ./target/*.jar service_discovery_eureka.jar
# copy the certificate into current directory
COPY ./src/main/resources/microservices.p12 microservices.p12
# start the jar with profile environment value
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${profile}", "service_discovery_eureka.jar"]

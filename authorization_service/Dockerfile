#https://docs.docker.com/engine/reference/builder/
#pull java (amazon coretto) image
FROM amazoncorretto:17
# define environment variables default value: dev
ENV PROFILE dev
# set workdir that the container is working is
WORKDIR /app/authorization_service
# copy generated jar file
COPY ./target/*.jar authorization_service.jar
#copy the certificates
COPY ./src/main/resources/microservices.p12 microservices.p12
# start service with set profile
# https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "authorization_service.jar"]

# https://docs.docker.com/build/building/multi-stage
# https://docs.docker.com/build/building/multi-stage/#name-your-build-stages
# multi-stage docker file
# stage 1
# we are defining this build as frontend-build 
# such taht we are able to 
# access it in stage2
FROM node:14.19-alpine3.14 AS frontend-build
WORKDIR /frontend-app
COPY . .
RUN npm i
RUN npm run build-docker

#stage 2
FROM nginx:alpine
COPY ./nginx_docker/microservices.crt /etc/pki/tls/certs/
COPY ./nginx_docker/microservice-without-passphrase.key /etc/pki/tls/private/
# here, we are copying the files that have been build with the npm build argument above 
# into the nginx directory such that it is displayed on the defined 
# port that is specified in the nginx config, which is
# also copied from the ./nginx_docker/conf directory
COPY --from=frontend-build /frontend-app/dist/frontend_service /usr/share/nginx/html
COPY ./nginx_docker/conf /etc/nginx/conf.d/
EXPOSE 80
EXPOSE 443
# postgres image
#https://hub.docker.com/_/postgres 
FROM postgres:14.4-alpine

#run init.sql at docker entrypoint to create a database
ADD init.sql /docker-entrypoint-initdb.d

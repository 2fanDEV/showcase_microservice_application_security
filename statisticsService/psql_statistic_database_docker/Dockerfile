# postgres image von den docker repositories
FROM postgres:14.4-alpine

#https://hub.docker.com/_/postgres
#run init.sql at docker entrypoint
ADD init.sql /docker-entrypoint-initdb.d

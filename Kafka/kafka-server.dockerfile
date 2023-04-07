FROM amazoncorretto:17
WORKDIR /usr/kafka/
COPY ../. /usr/kafka/
RUN chmod +x ./bin/zookeeper-server-start.sh 
RUN chmod +x ./bin/kafka-run-class.sh
RUN chmod +x ./bin/kafka-server-start.sh

CMD ["./bin/kafka-server-start.sh", "config/server.properties"]

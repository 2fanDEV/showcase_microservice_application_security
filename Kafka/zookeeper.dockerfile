FROM amazoncorretto:17
WORKDIR /usr/kafka/
COPY . .
RUN chmod +x ./bin/zookeeper-server-start.sh 
RUN chmod +x ./bin/kafka-run-class.sh
RUN chmod +x ./bin/kafka-server-start.sh

CMD ["./bin/zookeeper-server-start.sh", "config/zookeeper.properties"]

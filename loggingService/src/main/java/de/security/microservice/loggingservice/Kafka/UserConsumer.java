package de.security.microservice.loggingservice.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
/**
 * KafkaConsumer class for user server topics
 * https://docs.spring.io/spring-kafka/reference/html/#spring-boot-consumer-app
 */
@Component
public class UserConsumer {

    Logger logger = LoggerFactory.getLogger(UserConsumer.class);

    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * @param message
     */
    @KafkaListener(id = "USER_SMT_ID", topics = "USER_SERVER_SMT")
    public void simpleMessageListener(String message) {
        logger.info("new msg from eureka");
        logger.info("MESSAGE: " + message);
    }
    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * for the logs and create a file out of it to store it in a dedicated folder
     */
    @KafkaListener(id = "USER_BYTES", topics = "USER_SERVER_LOGS")
    public void byteListener(String[] bytesAsStringArr)
            throws IOException {
        logger.info("new bytes from user server arrived");
        logger.info("USER-SERVER: Start converting String to byte array");

        byte[] bytes = new byte[bytesAsStringArr.length];

        for (int i = 0; i < bytesAsStringArr.length; i++) {
            bytesAsStringArr[i] = bytesAsStringArr[i].replace("[", "")
                    .replace("]", "");
            bytes[i] = Byte.parseByte(bytesAsStringArr[i]);
        }

        Files.createDirectories(Path.of("./stored/user-server-logs"));

        String fileName = "user-server-log-" + Date.from(Instant.now()).getTime() + ".log";
        logger.info("created filename = " + fileName);
        Path of = Path.of("./stored/user-server-logs/" + fileName);
        /*
            For the Files.createFile(of) function
            https://www.baeldung.com/java-how-to-create-a-file#jdk7
         */
        try {
            Files.createFile(of);
        } catch (Exception e) {
            logger.info("Not being able to create file");
            return;
        }
        //https://docs.oracle.com/javase/tutorial/essential/io/file.html
        Files.write(of, bytes);

    }
}
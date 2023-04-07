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
 * KafkaConsumer class for authorization server topics
 * https://docs.spring.io/spring-kafka/reference/html/#spring-boot-consumer-app
 */
@Component
public class AuthorizationConsumer {

    Logger logger = LoggerFactory.getLogger(AuthorizationConsumer.class);


    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * @param message
     */
    @KafkaListener(id = "AUTHORIZATION_SMT_ID", topics = "AUTHORIZATION_SERVER_SMT")
    public void simpleMessageListener(String message) {
        logger.info("new msg from eureka");
        logger.info("MESSAGE: " + message);
    }

    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * for the logs and create a file out of it to store it in a dedicated folder
     */

    @KafkaListener(id = "AUTHORIZATION_BYTES", topics = "AUTHORIZATION_SERVER_LOGS")
    public void byteListener(String[] bytesAsStringArr)
            throws IOException {
        logger.info("new bytes from authorization server arrived");
        logger.info("AUTHORIZATION-SERVER: Start converting String to byte array");

        byte[] bytes = new byte[bytesAsStringArr.length];

        for (int i = 0; i < bytesAsStringArr.length; i++) {
            bytesAsStringArr[i] = bytesAsStringArr[i].replace("[", "")
                    .replace("]", "");
            bytes[i] = Byte.parseByte(bytesAsStringArr[i]);
        }

        //https://docs.oracle.com/javase/tutorial/essential/io/dirs.html
        Files.createDirectories(Path.of("./stored/authorization-server-logs"));

        String fileName = "authorization-server-log-" + Date.from(Instant.now()).getTime() + ".log";
        logger.info("created filename = " + fileName);
        Path of = Path.of("./stored/authorization-server-logs/" + fileName);
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
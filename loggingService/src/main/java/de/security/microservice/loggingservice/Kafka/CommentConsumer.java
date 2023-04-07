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
 * KafkaConsumer class for comment server topics
 * https://docs.spring.io/spring-kafka/reference/html/#spring-boot-consumer-app
 */
@Component
public class CommentConsumer {

    Logger logger = LoggerFactory.getLogger(CommentConsumer.class);

    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * @param message
     */
    @KafkaListener(id = "COMMENT_SMT_ID", topics = "COMMENT_SERVER_SMT")
    public void simpleMessageListener(String message) {
        logger.info("new msg from eureka");
        logger.info("MESSAGE: " + message);
    }
    /**
     * create listener that constantly looks if a new message appeared in the specific topic
     * for the logs and create a file out of it to store it in a dedicated folder
     */
    @KafkaListener(id = "COMMENT_BYTES", topics = "COMMENT_SERVER_LOGS")
    public void byteListener(String[] bytesAsStringArr)
            throws IOException {
        logger.info("new bytes from comment server arrived");
        logger.info("COMMENT-SERVER: Start converting String to byte array");

        byte[] bytes = new byte[bytesAsStringArr.length];

        for (int i = 0; i < bytesAsStringArr.length; i++) {
            bytesAsStringArr[i] = bytesAsStringArr[i].replace("[", "")
                    .replace("]", "");
            bytes[i] = Byte.parseByte(bytesAsStringArr[i]);
        }

        //https://docs.oracle.com/javase/tutorial/essential/io/dirs.html
        Files.createDirectories(Path.of("./stored/comment-server-logs"));

        String fileName = "comment-server-log-" + Date.from(Instant.now()).getTime() + ".log";
        logger.info("created filename = " + fileName);
        Path of = Path.of("./stored/comment-server-logs/" + fileName);
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
package de.security.microservice.token_service.Configuration.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;


/**
 * The function were created with the following
 * documentation:
 * https://docs.spring.io/spring-kafka/docs/current/reference/html/#getting-started
 */

/**
 * Here we are creating the configuration class responsible
 * to handle the connection to Kafka with
 * the org.springframework.kafka dependency,
 * which is declared in the pom.xml
 *
 * Furthermore, we also annotate
 * the class with @Configuration and @EnableScheduling
 * since we want to adjust the time interval in which the
 * log files are sent to Kafka
 */
@Configuration
@EnableScheduling
public class KafkaProducer {

    Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    // Declare a "KafkaTemplate" that sends towards message broker
    private final KafkaTemplate<String, String> template;

    @Autowired
    KafkaProducer(KafkaTemplate<String, String> template) { this.template = template; }

    /**
     * create a new topic at the message broker to
     * which we can send messages
     * (e.g: used to announce that something is coming)
     * @return {@link NewTopic}
     */
    @Bean
    public NewTopic gatewaySimpleMsgTopic()
    {
        return TopicBuilder.name("TOKEN_SERVER_SMT")
                .partitions(2)
                .replicas(1)
                .build();
    }

    /**
     * create a new topic at the message broker to
     * which we can send messages
     * used to send out the logs as bytes to the broker
     * @return {@link NewTopic}
     */
    @Bean
    public NewTopic gatewayLogBytesTopic()
    {
        return TopicBuilder.name("TOKEN_SERVER_LOGS")
                .partitions(2).replicas(1).build();
    }

    /**
     * this function is scheduled with cron
     * to be called every 5 minutes
     *
     * It gets the log-file from the respective folder
     * Reads it into a byte-array
     * and then sends the message to kafka and
     * deletes the logfile locally
     * @throws IOException
     */
    @Scheduled(cron = "5 * * * * *")
    public void sendMsgToTopic() throws IOException {

        logger.info("SENDING CURRENTLY LOGGED FILE TO LOGGING SERVICE");

        String fileLocation = "./scheduled/logs/tokenservice.log";
        Path fileLocationPath = Path.of(fileLocation);

        File file = new File(fileLocation);

        // I knew on how to read a file into the "File" object above,
        // however I was not sure on how to read the file
        // into a byte array
        // Ref:
        //https://howtodoinjava.com/java/io/read-file-content-into-byte-array/
        byte[] bytes = Files.readAllBytes(file.toPath());

        logger.info("SELECTED FILE: " + file.getName());
        logger.info("LENGTH: " + String.valueOf(bytes.length));

        this.template.send("TOKEN_SERVER_SMT", "transfering log files");
        this.template.send("TOKEN_SERVER_LOGS", Arrays.toString(bytes));

        //https://docs.oracle.com/javase/7/docs/api/java/nio/file/StandardOpenOption.html
        Files.write(fileLocationPath, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("CLEARING OLD LOCAL LOG CONTENT");

    }


}

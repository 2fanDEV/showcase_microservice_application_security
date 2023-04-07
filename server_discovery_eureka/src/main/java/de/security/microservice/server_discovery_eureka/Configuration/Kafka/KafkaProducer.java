package de.security.microservice.server_discovery_eureka.Configuration.Kafka;

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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Documentation:
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
    private final KafkaTemplate<String, String> template;

    @Autowired
    KafkaProducer(KafkaTemplate<String, String> template)
    {
        this.template = template;
    }


    /**
     * We are already connected with the Kafka Server
     * and tell it to create a Topic with a specific name
     * @return {@link NewTopic}
     */
    @Bean
    public NewTopic createServerEurekaLogTopic()
    {
        return TopicBuilder.name("EUREKA_SERVER_LOGS")
                .partitions(4)
                .replicas(1).build();
    }

    /**
     * We are already connected with the Kafka Server
     * and tell it to create a Topic with a specific name
     * @return {@link NewTopic}
     */
    @Bean
    public NewTopic serverEurekaSimpleMessageTopic()
    {
        return TopicBuilder.name("EUREKA_SMT")
                .partitions(1).replicas(1)
                .build();
    }


    /**
     * Creating a cronjob which is there to send the
     * logfiles to kafka in an interval of 5 minutes
     * @throws IOException
     */
    @Scheduled(cron = ("5 * * * * *"))
    public void runner2() throws IOException {
        logger.info("SENDING CURRENT LOGGED FILES TO LOGGING SERVICE");

        String fileLocation = "./scheduled/logs/eureka.log";
        Path fileLocationPath = Path.of(fileLocation);

        File file = new File(fileLocation);

        // I knew on how to read a file into the "File" object above,
        // however I was not sure on how to read the file
        // into a byte array
        // Ref:
        //https://howtodoinjava.com/java/io/read-file-content-into-byte-array/
        byte[] bytes = Files.readAllBytes(fileLocationPath);

        logger.info(file.getName());
        logger.info(String.valueOf(bytes.length));

        this.template.send("EUREKA_SMT", "transferring new log files");
        this.template.send("EUREKA_SERVER_LOGS", Arrays.toString(bytes));

        //https://docs.oracle.com/javase/7/docs/api/java/nio/file/StandardOpenOption.html
        Files.write(fileLocationPath, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("CLEARING OLD LOCAL LOG CONTENT");

    }
    

}

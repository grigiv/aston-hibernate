package ru.astondevs.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserProducer {
    private final KafkaTemplate kafkaTemplate;
    @Value("${kafka.topic.notification}")
    private String topicName;

    public void send(UserEmailEvent userEmailEvent) {
        try {
            kafkaTemplate.send(topicName, userEmailEvent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

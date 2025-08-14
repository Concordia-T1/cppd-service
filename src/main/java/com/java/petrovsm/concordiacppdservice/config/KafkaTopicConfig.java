package com.java.petrovsm.concordiacppdservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Определение топиков Kafka
    public static final String TOPIC_CANDIDATE_NOTIFICATIONS = "concordia-candidate-notifications";
    public static final String TOPIC_MANAGER_NOTIFICATIONS = "concordia-manager-notifications";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic candidateNotificationsTopic() {
        return new NewTopic(TOPIC_CANDIDATE_NOTIFICATIONS, 1, (short) 1);
    }

    @Bean
    public NewTopic managerNotificationsTopic() {
        return new NewTopic(TOPIC_MANAGER_NOTIFICATIONS, 1, (short) 1);
    }
}

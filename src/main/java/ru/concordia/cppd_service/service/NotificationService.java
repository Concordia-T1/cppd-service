package ru.concordia.cppd_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import ru.concordia.cppd_service.config.KafkaTopicConfig;
import ru.concordia.cppd_service.dto.CandidateNotificationDto;
import ru.concordia.cppd_service.dto.ManagerNotificationDto;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCandidateNotification(CandidateNotificationDto notification) {
        log.info("Sending notification to candidate: {}", notification.getRecipientEmail());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopicConfig.TOPIC_CANDIDATE_NOTIFICATIONS,
                notification.getRecipientEmail(),
                notification
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.trace("Notification successfully sent to candidate: {}. Offset: {}",
                        notification.getRecipientEmail(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error sending notification to candidate: {}",
                        notification.getRecipientEmail(), ex);
            }
        });
    }

    public void sendManagerNotification(ManagerNotificationDto notification) {
        log.info("Sending notification to manager with UUID: {}", notification.getManagerUUID());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopicConfig.TOPIC_MANAGER_NOTIFICATIONS,
                notification.getManagerUUID(),
                notification
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.trace("Notification successfully sent to manager with UUID: {}. Offset: {}",
                        notification.getManagerUUID(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error sending notification to manager with UUID: {}",
                        notification.getManagerUUID(), ex);
            }
        });
    }

    public CandidateNotificationDto createCandidateNotification(
            String recipientEmail,
            String senderUUID,
            String subject,
            String content) {

        return CandidateNotificationDto.builder()
                .recipientEmail(recipientEmail)
                .senderUUID(senderUUID)
                .subject(subject)
                .content(content)
                .build();
    }

    public ManagerNotificationDto createManagerNotification(
            String managerUUID,
            String subject,
            String content) {

        return ManagerNotificationDto.builder()
                .managerUUID(managerUUID)
                .subject(subject)
                .content(content)
                .build();
    }
}

package ru.concordia.cppd_service.api.v1.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import ru.concordia.cppd_service.config.KafkaTopicConfig;
import ru.concordia.cppd_service.dto.CandidateNotificationDto;
import ru.concordia.cppd_service.dto.ManagerNotificationDto;

import java.util.Collections;
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
                log.info("Notification successfully sent to candidate: {}. Offset: {}",
                        notification.getRecipientEmail(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error sending notification to candidate: {}",
                        notification.getRecipientEmail(), ex);
            }
        });
    }

    public void sendManagerNotification(ManagerNotificationDto notification) {
        log.info("Sending notification to manager: {}", notification.getManagerEmail());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopicConfig.TOPIC_MANAGER_NOTIFICATIONS,
                notification.getManagerEmail(),
                notification
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification successfully sent to manager: {}. Offset: {}",
                        notification.getManagerEmail(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error sending notification to manager: {}",
                        notification.getManagerEmail(), ex);
            }
        });
    }

    public CandidateNotificationDto createCandidateNotification(
            String recipientEmail,
            String senderEmail,
            String subject,
            String content) {

        return CandidateNotificationDto.builder()
                .recipientEmail(recipientEmail)
                .senderEmail(senderEmail)
                .subject(subject)
                .content(content)
                .build();
    }

    public ManagerNotificationDto createManagerNotification(
            String candidateEmail,
            String managerEmail,
            String subject,
            String content) {

        return ManagerNotificationDto.builder()
                .notificationType(Collections.singletonList("EMAIL"))
                .candidateEmail(candidateEmail)
                .managerEmail(managerEmail)
                .managerTelegram(null)
                .managerWhatsApp(null)
                .subject(subject)
                .content(content)
                .build();
    }
}

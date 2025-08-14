package com.java.petrovsm.concordiacppdservice.service;

import com.java.petrovsm.concordiacppdservice.config.KafkaTopicConfig;
import com.java.petrovsm.concordiacppdservice.dto.CandidateNotificationDto;
import com.java.petrovsm.concordiacppdservice.dto.ClaimStatusChangeDto;
import com.java.petrovsm.concordiacppdservice.dto.ManagerNotificationDto;
import com.java.petrovsm.concordiacppdservice.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Отправляет уведомление об изменении статуса заявки в Kafka
     *
     * @param claim Заявка с обновленным статусом
     * @param previousStatus Предыдущий статус заявки
     */
    public void notifyClaimStatusChanged(Claim claim, Claim.ClaimStatus previousStatus) {

        ClaimStatusChangeDto statusChangeDto = ClaimStatusChangeDto.builder()
                .claimId(claim.getId())
                .candidateEmail(claim.getCandidateEmail())
                //.managerEmail(claim.getManager().getEmail()) тут надо обратиться к сущьности, но у нас ее данные в сервисе авторизации
                .previousStatus(previousStatus)
                .currentStatus(claim.getStatus())
                .changedAt(LocalDateTime.now())
                .build();

        log.info("Отправка уведомления об изменении статуса заявки: {}", statusChangeDto);

        // В зависимости от статуса отправляем разные уведомления
        if (claim.getStatus() == Claim.ClaimStatus.CONSENT ||
            claim.getStatus() == Claim.ClaimStatus.REFUSED) {
            sendManagerNotification(statusChangeDto);
        }
    }


    /**
     * Отправляет уведомление кандидату
     * @param recipientEmail Email кандидата
     * @param senderEmail Email менеджера
     * @param subject Тема письма
     * @param content Содержимое письма
     */
    public void sendCandidateNotification(String recipientEmail, String senderEmail, String subject, String content) {
        CandidateNotificationDto notificationDto = CandidateNotificationDto.builder()
                .recipientEmail(recipientEmail)
                .senderEmail(senderEmail)
                .subject(subject)
                .content(content)
                .build();

        log.info("Отправка уведомления кандидату: {}", notificationDto);
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_CANDIDATE_NOTIFICATIONS, notificationDto);
    }

    /**
     * Отправляет уведомление менеджеру о решении кандидата
     * @param statusChangeDto Информация об изменении статуса заявки
     */
    private void sendManagerNotification(ClaimStatusChangeDto statusChangeDto) {
        String status = statusChangeDto.getCurrentStatus() == Claim.ClaimStatus.CONSENT ? "согласие" : "отказ";

        ManagerNotificationDto notificationDto = ManagerNotificationDto.builder()
                .notificationType(Collections.singletonList("EMAIL"))
                .candidateEmail(statusChangeDto.getCandidateEmail())
                .managerEmail(statusChangeDto.getManagerEmail())
                .managerTelegram(null)
                .managerWhatsApp(null)
                .subject("Ответ на запрос согласия на обработку персональных данных")
                .content(String.format("Кандидат %s дал %s на обработку персональных данных",
                        statusChangeDto.getCandidateEmail(), status))
                .build();

        log.info("Отправка уведомления менеджеру: {}", notificationDto);
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_MANAGER_NOTIFICATIONS, notificationDto);
    }
}

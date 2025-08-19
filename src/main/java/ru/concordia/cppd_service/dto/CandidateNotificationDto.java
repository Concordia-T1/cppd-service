package ru.concordia.cppd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateNotificationDto {
    private String recipientEmail;
    private String senderEmail;
    // Base64url encoded encrypted X-User-SMTP-Key
    private String senderSmtpKey;
    private String subject;
    private String content;
}

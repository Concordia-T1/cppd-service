package com.java.petrovsm.concordiacppdservice.dto;

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
    private String subject;
    private String content;
}

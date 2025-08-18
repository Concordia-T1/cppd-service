package ru.concordia.cppd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerNotificationDto {
    private List<String> notificationType;
    private String candidateEmail;
    private String managerEmail;
    private String managerTelegram;
    private String managerWhatsApp;
    private String subject;
    private String content;
}

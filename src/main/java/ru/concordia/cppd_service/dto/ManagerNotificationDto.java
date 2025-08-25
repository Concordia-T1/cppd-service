package ru.concordia.cppd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerNotificationDto {
    private String managerUUID;
    private String subject;
    private String content;
}

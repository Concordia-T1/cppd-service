package ru.concordia.cppd_service.dto;

import ru.concordia.cppd_service.model.Claim;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusChangeDto {
    private Long claimId;
    private String candidateEmail;
    private String managerEmail;
    private Claim.ClaimStatus previousStatus;
    private Claim.ClaimStatus currentStatus;
    private LocalDateTime changedAt;
}

package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.concordia.cppd_service.model.Claim;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Claim")
public class ClaimRecord {
    @Schema(description = "UUIDv7")
    private UUID id;

    @Schema(description = "Owner UUIDv7")
    private UUID owner_id;

    @Schema(description = "Owner email")
    private String owner_email;

    @Schema(description = "Candidate email")
    private String candidate_email;

    @Schema(description = "Candidate last name")
    private String candidate_last_name;

    @Schema(description = "Candidate first name")
    private String candidate_first_name;

    @Schema(description = "Candidate middle name")
    private String candidate_middle_name;

    @Schema(description = "Candidate birthdate")
    private String candidate_birthdate;

    @Schema(description = "Candidate phone")
    private String candidate_phone;

    @Schema(description = "Template UUIDv7")
    private UUID template_id;

    @Schema(description = "Status")
    private Claim.ClaimStatus status;

    @Schema(description = "Responded at")
    private LocalDateTime responded_at;

    @Schema(description = "Expires at")
    private LocalDateTime expires_at;

    @Schema(description = "Created at")
    private LocalDateTime created_at;

    @Schema(description = "Updated at")
    private LocalDateTime updated_at;
}

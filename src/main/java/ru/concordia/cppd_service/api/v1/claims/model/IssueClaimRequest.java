package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Issue claim request")
public class IssueClaimRequest {
    @Schema(description = "Candidates emails")
    @NotEmpty(message = "Candidates emails list cannot be empty")
    private List<@Email(message = "Each candidate email must be well-formed") String> candidates_emails;

    @Schema(description = "Manager email")
    @NotBlank(message = "Manager email cannot be empty")
    @Email(message = "Must be a well-formed email address")
    private String manager_email;

    @Schema(description = "Template UUIDv7")
    @NotNull(message = "Template UUIDv7 cannot be empty")
    private UUID template_id;
}

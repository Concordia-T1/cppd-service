package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.concordia.cppd_service.validation.ValidEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Act claim request")
public class ActClaimRequest {
    @Schema(description = "Claim ID")
    @NotNull(message = "Claim ID cannot be empty")
    private Long claim_id;

    @Schema(description = "SIG (HMAC-SHA256 signature)")
    @NotBlank(message = "SIG cannot be empty")
    private String sig;

    @Schema(description = "Candidate last name")
    @NotBlank(message = "Candidate last name can't be empty")
    @Size(max = 50, message = "Candidate last name cannot exceed 50 characters")
    private String last_name;

    @Schema(description = "Candidate first name")
    @NotBlank(message = "Candidate first name can't be empty")
    @Size(max = 50, message = "Candidate first name cannot exceed 50 characters")
    private String first_name;

    @Schema(description = "Candidate middle name")
    @NotBlank(message = "Candidate middle name can't be empty")
    @Size(max = 50, message = "Candidate middle name cannot exceed 50 characters")
    private String middle_name;

    @Schema(description = "Candidate phone")
    @NotBlank(message = "Candidate phone can't be empty")
    @Size(max = 15, message = "Candidate phone cannot exceed 15 characters")
    @Pattern(regexp = "^[1-9][0-9]{7,14}$", message = "Candidate phone must be well-formed, e.g: '78001234567'")
    private String phone;

    @Schema(description = "State")
    @NotBlank(message = "State can't be empty")
    @ValidEnum(enumClass = ActState.class, ignoreCase = true, message = "State must be 'ACT_CONSENT' or 'ACT_REFUSED'")
    private String state;

    public ActState getState() {
        return Enum.valueOf(ActState.class, this.state.toUpperCase());
    }
}
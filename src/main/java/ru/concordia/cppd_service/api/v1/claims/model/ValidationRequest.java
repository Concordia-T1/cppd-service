package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "URI validation request")
public class ValidationRequest {
    @Schema(description = "EPK (Ephemeral public key)")
    @NotBlank(message = "EPK cannot be empty")
    private String epk;

    @Schema(description = "CTX (Context)")
    @NotBlank(message = "CTX cannot be empty")
    private String ctx;

    @Schema(description = "SIG (HMAC-SHA256 signature)")
    @NotBlank(message = "SIG cannot be empty")
    private String sig;
}

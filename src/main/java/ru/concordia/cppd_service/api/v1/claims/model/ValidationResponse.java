package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "URI validation response")
public class ValidationResponse extends SuccessResponse {
    @Schema(description = "Claim UUIDv7")
    private final UUID claim_id;
}

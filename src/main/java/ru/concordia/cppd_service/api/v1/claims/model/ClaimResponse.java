package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Claim response")
public class ClaimResponse extends SuccessResponse {
    @Schema(description = "Claim")
    private final ClaimRecord claim;
}

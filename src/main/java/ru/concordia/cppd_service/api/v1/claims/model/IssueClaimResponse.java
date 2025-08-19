package ru.concordia.cppd_service.api.v1.claims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Claim issue response")
public class IssueClaimResponse extends SuccessResponse {
    @Schema(description = "List of claims")
    @Singular("claim")
    private final List<ClaimRecord> claims;
}

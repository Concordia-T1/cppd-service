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
@Schema(description = "Claims collection response")
public class ClaimsCollectionResponse extends SuccessResponse {
    @Schema(description = "Page id")
    private final int page_id;

    @Schema(description = "Page size")
    private final int page_size;

    @Schema(description = "Total remaining elements")
    private final long total_elements;

    @Schema(description = "Total remaining pages")
    private final int total_pages;

    @Schema(description = "List of claims")
    @Singular("claim")
    private final List<ClaimRecord> claims;
}

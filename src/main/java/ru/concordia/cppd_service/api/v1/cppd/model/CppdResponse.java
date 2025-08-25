package ru.concordia.cppd_service.api.v1.cppd.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Cppd response")
public class CppdResponse extends SuccessResponse {
    @Schema(description = "UUIDv7")
    private UUID id;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}

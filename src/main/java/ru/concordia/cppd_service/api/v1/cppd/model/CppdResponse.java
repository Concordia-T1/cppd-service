package ru.concordia.cppd_service.api.v1.cppd.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Cppd response")
public class CppdResponse extends SuccessResponse {
    @Schema(description = "Account")
    private Long id;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}

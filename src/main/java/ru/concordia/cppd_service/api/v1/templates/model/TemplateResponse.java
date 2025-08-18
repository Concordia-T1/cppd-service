package ru.concordia.cppd_service.api.v1.templates.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Template response")
public class TemplateResponse extends SuccessResponse {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Owner ID")
    private Long owner_id;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Subject")
    private String subject;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Created at")
    private LocalDateTime created_at;

    @Schema(description = "Updated at")
    private LocalDateTime updated_at;
}

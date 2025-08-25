package ru.concordia.cppd_service.api.v1.templates.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Template")
public class TemplateRecord {
    @Schema(description = "UUIDv7")
    private UUID id;

    @Schema(description = "Owner UUIDv7")
    private UUID owner_id;

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

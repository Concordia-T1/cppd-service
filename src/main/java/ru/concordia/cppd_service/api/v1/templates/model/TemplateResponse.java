package ru.concordia.cppd_service.api.v1.templates.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Template response")
public class TemplateResponse extends SuccessResponse {
    @Schema(description = "Template")
    private TemplateRecord template;
}

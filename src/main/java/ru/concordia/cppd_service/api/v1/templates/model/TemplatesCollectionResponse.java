package ru.concordia.cppd_service.api.v1.templates.model;

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
@Schema(description = "Template response")
public class TemplatesCollectionResponse extends SuccessResponse {
    @Schema(description = "Page id")
    private int page_id;

    @Schema(description = "Page size")
    private int page_size;

    @Schema(description = "Total remaining elements")
    private final long total_elements;

    @Schema(description = "Total remaining pages")
    private final int total_pages;

    @Schema(description = "List of templates")
    @Singular("templates")
    private List<TemplateRecord> templates;
}

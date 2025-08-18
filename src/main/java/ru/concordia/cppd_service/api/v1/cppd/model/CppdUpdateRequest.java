package ru.concordia.cppd_service.api.v1.cppd.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cppd update request")
public class CppdUpdateRequest {
    @Schema(description = "Content")
    @NotBlank(message = "Content can't be empty")
    private String content;
}

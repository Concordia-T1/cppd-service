package ru.concordia.cppd_service.api.v1.templates.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {
    @Schema(description = "Name")
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Schema(description = "Subject")
    @NotBlank(message = "Subject cannot be empty")
    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    private String subject;

    @Schema(description = "Content")
    @NotBlank(message = "Content cannot be empty")
    private String content;
}

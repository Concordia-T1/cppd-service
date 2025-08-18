package ru.concordia.cppd_service.dto;

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
public class TemplateRequest {

    @NotBlank(message = "Template name cannot be empty")
    @Size(max = 255, message = "Template name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Email subject cannot be empty")
    @Size(max = 255, message = "Email subject cannot exceed 255 characters")
    private String subject;

    @NotBlank(message = "Template content cannot be empty")
    private String content;
}

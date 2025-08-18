package ru.concordia.cppd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplatesCollectionResponse {
    private int pageNumber;
    private int pageSize;
    private List<TemplateResponse> templates;
}

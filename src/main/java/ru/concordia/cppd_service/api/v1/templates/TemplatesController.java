package ru.concordia.cppd_service.api.v1.templates;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.concordia.cppd_service.api.v1.templates.model.CreateTemplateRequest;
import ru.concordia.cppd_service.api.v1.templates.model.TemplateResponse;
import ru.concordia.cppd_service.api.v1.templates.model.TemplatesCollectionResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cppd-service/v1/templates")
public class TemplatesController {
    private final TemplatesService templatesService;

    @GetMapping
    public ResponseEntity<TemplatesCollectionResponse> getAllTemplates(
            @PageableDefault(size = 50, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Request to get all templates");
        return templatesService.getAllTemplates(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(
            @PathVariable UUID id
    ) {
        log.info("Request to get templates with ID: {}", id);
        return templatesService.getTemplateById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<TemplateResponse> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request,
            @RequestHeader("X-User-ID") UUID principalId
    ) {
        log.info("Request to create new templates: {}", request.getName());
        return templatesService.createTemplate(request, principalId);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTemplateRequest request
    ) {
        log.info("Request to update templates with ID {}: {}", id, request.getName());
        return templatesService.updateTemplate(id, request);
    }
}

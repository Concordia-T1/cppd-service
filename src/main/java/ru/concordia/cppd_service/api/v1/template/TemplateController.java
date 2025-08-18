package ru.concordia.cppd_service.api.v1.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ru.concordia.cppd_service.dto.TemplateRequest;
import ru.concordia.cppd_service.dto.TemplateResponse;
import ru.concordia.cppd_service.dto.TemplatesCollectionResponse;
import ru.concordia.cppd_service.api.v1.template.service.TemplateService;

import org.springframework.data.domain.Sort;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<TemplatesCollectionResponse> getAllTemplates(
            @PageableDefault(size = 50, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Request to get all templates");
        return templateService.getAllTemplates(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable Long id) {
        log.info("Request to get template with ID: {}", id);
        return templateService.getTemplateById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody TemplateRequest request) {
        log.info("Request to create new template: {}", request.getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        return templateService.createTemplate(request, userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateRequest request) {
        log.info("Request to update template with ID {}: {}", id, request.getName());
        return templateService.updateTemplate(id, request);
    }
}

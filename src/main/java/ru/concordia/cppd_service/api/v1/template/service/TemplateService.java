package ru.concordia.cppd_service.api.v1.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.concordia.cppd_service.dto.TemplateRequest;
import ru.concordia.cppd_service.dto.TemplateResponse;
import ru.concordia.cppd_service.dto.TemplatesCollectionResponse;
import ru.concordia.cppd_service.model.Template;
import ru.concordia.cppd_service.repository.TemplateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public ResponseEntity<TemplatesCollectionResponse> getAllTemplates(Pageable pageable) {
        Page<Template> templatesPage = templateRepository.findAll(pageable);

        TemplatesCollectionResponse response = TemplatesCollectionResponse.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .templates(templatesPage.getContent().stream()
                        .map(this::mapToTemplateResponse)
                        .toList())
                .build();

        log.info("Retrieved templates list, page {}, page size {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<TemplateResponse> getTemplateById(Long id) {
        Optional<Template> template = templateRepository.findById(id);

        if (template.isEmpty()) {
            log.warn("Template with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Retrieved template with ID {}: {}", id, template.get().getName());
        return ResponseEntity.ok(mapToTemplateResponse(template.get()));
    }

    @Transactional
    public ResponseEntity<TemplateResponse> createTemplate(TemplateRequest request, Long ownerId) {
        Template template = Template.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .content(request.getContent())
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();

        Template savedTemplate = templateRepository.save(template);
        log.info("Created new template with ID {}: {}", savedTemplate.getId(), savedTemplate.getName());

        return ResponseEntity.ok(mapToTemplateResponse(savedTemplate));
    }

    @Transactional
    public ResponseEntity<TemplateResponse> updateTemplate(Long id, TemplateRequest request) {
        Optional<Template> existingTemplate = templateRepository.findById(id);

        if (existingTemplate.isEmpty()) {
            log.warn("Attempt to update non-existing template with ID {}", id);
            return ResponseEntity.notFound().build();
        }

        Template template = existingTemplate.get();
        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setUpdatedAt(LocalDateTime.now());

        Template updatedTemplate = templateRepository.save(template);
        log.info("Updated template with ID {}: {}", updatedTemplate.getId(), updatedTemplate.getName());

        return ResponseEntity.ok(mapToTemplateResponse(updatedTemplate));
    }

    public ResponseEntity<TemplatesCollectionResponse> getTemplatesByOwnerId(Long ownerId, Pageable pageable) {
        List<Template> templates = templateRepository.findByOwnerId(ownerId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), templates.size());
        List<Template> paginatedTemplates = templates.subList(start, end);

        TemplatesCollectionResponse response = TemplatesCollectionResponse.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .templates(paginatedTemplates.stream()
                        .map(this::mapToTemplateResponse)
                        .toList())
                .build();

        log.info("Retrieved templates for owner ID {}, page {}, page size {}",
                ownerId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(response);
    }

    private TemplateResponse mapToTemplateResponse(Template template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .ownerId(template.getOwnerId())
                .name(template.getName())
                .subject(template.getSubject())
                .content(template.getContent())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}

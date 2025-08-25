package ru.concordia.cppd_service.api.v1.templates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concordia.cppd_service.api.v1.templates.exceptions.TemplateNotFoundException;
import ru.concordia.cppd_service.api.v1.templates.model.CreateTemplateRequest;
import ru.concordia.cppd_service.api.v1.templates.model.TemplateRecord;
import ru.concordia.cppd_service.api.v1.templates.model.TemplateResponse;
import ru.concordia.cppd_service.api.v1.templates.model.TemplatesCollectionResponse;
import ru.concordia.cppd_service.model.Template;
import ru.concordia.cppd_service.repository.TemplateRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplatesService {
    private final TemplateRepository templateRepository;

    public ResponseEntity<TemplatesCollectionResponse> getAllTemplates(Pageable pageable) {
        Page<Template> templatesPage = templateRepository.findAll(pageable);

        TemplatesCollectionResponse response = TemplatesCollectionResponse.builder()
                .page_id(pageable.getPageNumber())
                .page_size(pageable.getPageSize())
                .total_elements(templatesPage.getTotalElements())
                .total_pages(templatesPage.getTotalPages())
                .templates(templatesPage.getContent().stream()
                        .map(this::mapToTemplateRecord)
                        .toList())
                .build();

        log.info("Retrieved templates list, page {}, page size {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<TemplateResponse> getTemplateById(UUID id) {
        final var template = templateRepository.findById(id)
                .orElseThrow(TemplateNotFoundException::new);

        log.info("Retrieved template with ID {}: {}", id, template.getName());

        return ResponseEntity.ok(TemplateResponse.builder()
                .template(mapToTemplateRecord(template))
                .build());
    }

    @Transactional
    public ResponseEntity<TemplateResponse> createTemplate(CreateTemplateRequest request, UUID ownerId, String principalRole) {
        assertPermission(Objects.equals(principalRole, "ROLE_ADMIN"));

        Template template = Template.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .content(request.getContent())
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();

        Template savedTemplate = templateRepository.save(template);
        log.info("Created new template with ID {}: {}", savedTemplate.getId(), savedTemplate.getName());

        return ResponseEntity.ok(TemplateResponse.builder()
                .template(mapToTemplateRecord(savedTemplate))
                .build());
    }

    @Transactional
    public ResponseEntity<TemplateResponse> updateTemplate(UUID id, CreateTemplateRequest request, String principalRole) {
        assertPermission(Objects.equals(principalRole, "ROLE_ADMIN"));

        final var template = templateRepository.findById(id)
                .orElseThrow(TemplateNotFoundException::new);

        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setUpdatedAt(LocalDateTime.now());

        Template updatedTemplate = templateRepository.save(template);
        log.info("Updated template with ID {}: {}", updatedTemplate.getId(), updatedTemplate.getName());

        return ResponseEntity.ok(TemplateResponse.builder()
                .template(mapToTemplateRecord(updatedTemplate))
                .build());
    }

    public ResponseEntity<TemplatesCollectionResponse> getTemplatesByOwnerId(UUID ownerId, Pageable pageable) {
        final var templatesPage = templateRepository.findByOwnerId(ownerId, pageable);
        final var templates = templatesPage.getContent();

        TemplatesCollectionResponse response = TemplatesCollectionResponse.builder()
                .page_id(pageable.getPageNumber())
                .page_size(pageable.getPageSize())
                .total_elements(templatesPage.getTotalElements())
                .total_pages(templatesPage.getTotalPages())
                .templates(templates.stream()
                        .map(this::mapToTemplateRecord)
                        .toList())
                .build();

        log.info("Retrieved template for owner ID {}, page {}, page size {}",
                ownerId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(response);
    }

    private TemplateRecord mapToTemplateRecord(Template template) {
        return TemplateRecord.builder()
                .id(template.getId())
                .owner_id(template.getOwnerId())
                .name(template.getName())
                .subject(template.getSubject())
                .content(template.getContent())
                .created_at(template.getCreatedAt())
                .updated_at(template.getUpdatedAt())
                .build();
    }

    private void assertPermission(boolean condition) {
        if (!condition)
            throw new AccessDeniedException("Insufficient rights");
    }
}

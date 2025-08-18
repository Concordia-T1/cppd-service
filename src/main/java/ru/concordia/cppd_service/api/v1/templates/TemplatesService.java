package ru.concordia.cppd_service.api.v1.templates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.concordia.cppd_service.api.v1.templates.model.CreateTemplateRequest;
import ru.concordia.cppd_service.api.v1.templates.model.TemplateResponse;
import ru.concordia.cppd_service.api.v1.templates.model.TemplatesCollectionResponse;
import ru.concordia.cppd_service.model.Template;
import ru.concordia.cppd_service.repository.TemplateRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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
            // todo! адаптировать под наш BaseResponse, а именно ErrorResponse,
            //  либо выкидывать ошибку, убедиться что она обрабатывается в CppdServiceExceptionHandler
            return ResponseEntity.notFound().build();
        }

        log.info("Retrieved template with ID {}: {}", id, template.get().getName());
        return ResponseEntity.ok(mapToTemplateResponse(template.get()));
    }

    @Transactional
    public ResponseEntity<TemplateResponse> createTemplate(CreateTemplateRequest request, Long ownerId, String principalRole) {
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

        return ResponseEntity.ok(mapToTemplateResponse(savedTemplate));
    }

    @Transactional
    public ResponseEntity<TemplateResponse> updateTemplate(Long id, CreateTemplateRequest request, String principalRole) {
        assertPermission(Objects.equals(principalRole, "ROLE_ADMIN"));

        Optional<Template> existingTemplate = templateRepository.findById(id);

        if (existingTemplate.isEmpty()) {
            log.warn("Attempt to update non-existing templates with ID {}", id);
            // todo! адаптировать под наш BaseResponse, а именно ErrorResponse,
            //  либо выкидывать ошибку, убедиться что она обрабатывается в CppdServiceExceptionHandler
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
        // todo! зачем мы принимаем pageable если всё равно загружем разом все модели с БД?
//        List<Template> templates = templateRepository.findByOwnerId(ownerId);
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), templates.size());
//        List<Template> paginatedTemplates = templates.subList(start, end);

        // todo! корректный подход:
        final var templates = templateRepository.findByOwnerId(ownerId, pageable).getContent();

        TemplatesCollectionResponse response = TemplatesCollectionResponse.builder()
                .page_id(pageable.getPageNumber())
                .page_size(pageable.getPageSize())
                .templates(templates.stream()
                        .map(this::mapToTemplateResponse)
                        .toList())
                .build();

        log.info("Retrieved template for owner ID {}, page {}, page size {}",
                ownerId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(response);
    }

    private TemplateResponse mapToTemplateResponse(Template template) {
        return TemplateResponse.builder()
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
        if (!condition) {
            throw new AccessDeniedException("Insufficient rights");
        }
    }
}

package ru.concordia.cppd_service.api.v1.cppd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concordia.cppd_service.dto.CPPDResponse;
import ru.concordia.cppd_service.dto.CPPDUpdateRequest;
import ru.concordia.cppd_service.model.Cppd;
import ru.concordia.cppd_service.repository.CppdRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CppdService {

    private final CppdRepository cppdRepository;

    public ResponseEntity<CPPDResponse> getCppdTemplate() {
        log.info("Fetching current CPPD template");

         Cppd cppd = cppdRepository.findById(1L)
                .orElseGet(() -> {
                    log.info("No CPPD template found, creating default one");
                    Cppd defaultCppd = Cppd.builder()
                            .content("Default CPPD template content")
                            .createdAt(LocalDateTime.now())
                            .build();
                    return cppdRepository.save(defaultCppd);
                });

        return ResponseEntity.ok(mapToResponse(cppd));
    }

    @Transactional
    public ResponseEntity<CPPDResponse> updateCppdTemplate(CPPDUpdateRequest request) {
        log.info("Updating CPPD template");

        if (request.getContent() == null || request.getContent().isBlank()) {
            log.error("Invalid CPPD content: content cannot be empty");
            return ResponseEntity.badRequest().build();
        }

        Cppd cppd = cppdRepository.findById(1L)
                .orElseGet(() -> Cppd.builder()
                        .createdAt(LocalDateTime.now())
                        .build());

        cppd.setContent(request.getContent());
        cppd.setUpdatedAt(LocalDateTime.now());

        Cppd savedCppd = cppdRepository.save(cppd);
        log.info("CPPD template updated successfully with ID: {}", savedCppd.getId());

        return ResponseEntity.ok(mapToResponse(savedCppd));
    }

    private CPPDResponse mapToResponse(Cppd cppd) {
        return CPPDResponse.builder()
                .id(cppd.getId())
                .content(cppd.getContent())
                .createdAt(cppd.getCreatedAt())
                .updatedAt(cppd.getUpdatedAt())
                .build();
    }
}

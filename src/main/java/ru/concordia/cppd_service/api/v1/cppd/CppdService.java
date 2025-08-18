package ru.concordia.cppd_service.api.v1.cppd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concordia.cppd_service.api.v1.cppd.model.CppdResponse;
import ru.concordia.cppd_service.api.v1.cppd.model.CppdUpdateRequest;
import ru.concordia.cppd_service.model.Cppd;
import ru.concordia.cppd_service.repository.CppdRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CppdService {
    private final CppdRepository cppdRepository;

    public ResponseEntity<CppdResponse> getCppdTemplate() {
        log.info("Fetching current CPPD templates");

        Cppd cppd = cppdRepository.findById(1L)
                .orElseGet(() -> {
                    // todo! мы можем вынести создание дефолтного СОПД в changeset liquibase,
                    //  как это уже реализовано в auth-service
                    log.info("No CPPD templates found, creating default one");
                    Cppd defaultCppd = Cppd.builder()
                            .content("Default CPPD templates content")
                            .createdAt(LocalDateTime.now())
                            .build();
                    return cppdRepository.save(defaultCppd);
                });

        return ResponseEntity.ok(mapToResponse(cppd));
    }

    @Transactional
    public ResponseEntity<CppdResponse> updateCppdTemplate(CppdUpdateRequest request, String principalRole) {
        assertPermission(Objects.equals(principalRole, "ROLE_ADMIN"));

        log.info("Updating CPPD templates");

        if (request.getContent() == null || request.getContent().isBlank()) {
            log.error("Invalid CPPD content: content cannot be empty");
            // todo! адаптировать под наш BaseResponse, а именно ErrorResponse,
            //  либо выкидывать ошибку, убедиться что она обрабатывается в CppdServiceExceptionHandler
            return ResponseEntity.badRequest().build();
        }

        Cppd cppd = cppdRepository.findById(1L)
                .orElseGet(() -> Cppd.builder()
                        .createdAt(LocalDateTime.now())
                        .build());

        cppd.setContent(request.getContent());
        cppd.setUpdatedAt(LocalDateTime.now());

        Cppd savedCppd = cppdRepository.save(cppd);
        log.info("CPPD templates updated successfully with ID: {}", savedCppd.getId());

        return ResponseEntity.ok(mapToResponse(savedCppd));
    }

    private CppdResponse mapToResponse(Cppd cppd) {
        return CppdResponse.builder()
                .id(cppd.getId())
                .content(cppd.getContent())
                .createdAt(cppd.getCreatedAt())
                .updatedAt(cppd.getUpdatedAt())
                .build();
    }

    private void assertPermission(boolean condition) {
        if (!condition) {
            throw new AccessDeniedException("Insufficient rights");
        }
    }
}

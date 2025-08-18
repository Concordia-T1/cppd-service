package ru.concordia.cppd_service.api.v1.claims;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.api.v1.claims.model.IssueResponse;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;
import ru.concordia.cppd_service.api.v1.claims.exceptions.ClaimNotFoundException;
import ru.concordia.cppd_service.api.v1.claims.model.ClaimResponse;
import ru.concordia.cppd_service.api.v1.claims.model.ClaimsCollectionResponse;
import ru.concordia.cppd_service.repository.ClaimRepository;
import ru.concordia.cppd_service.service.EcdhLinkService;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimsService {
    private final ClaimRepository claimRepository;
    private final EcdhLinkService ecdhLinkService;

    public ResponseEntity<ClaimsCollectionResponse> collection(String principalRole, Pageable pageable) {
        assertPermission(Objects.equals(principalRole, "ROLE_ADMIN"));

        final var claimsPage = claimRepository.findAll(pageable);
        final var claims = claimsPage.getContent();

        return ResponseEntity.ok(
                ClaimsCollectionResponse.builder()
                        .page_id(pageable.getPageNumber())
                        .page_size(pageable.getPageSize())
                        .total_elements(claimsPage.getTotalElements())
                        .total_pages(claimsPage.getTotalPages())
                        .claims(claims)
                        .build()
        );
    }

    public ResponseEntity<ClaimResponse> scalar(Long id, Long principalId, String principalRole) {
        final var claim = claimRepository.findById(id)
                .orElseThrow(ClaimNotFoundException::new);

        assertPermission(Objects.equals(claim.getOwnerId(), principalId)
                || Objects.equals(principalRole, "ROLE_ADMIN"));

        return ResponseEntity.ok(
                ClaimResponse.builder()
                        .claim(claim)
                        .build()
        );
    }

    public ResponseEntity<ClaimsCollectionResponse> myCollection(Long principalId, Pageable pageable) {
        final var claimsPage = claimRepository.findByOwnerId(principalId, pageable);
        final var claims = claimsPage.getContent();

        return ResponseEntity.ok(
                ClaimsCollectionResponse.builder()
                        .page_id(pageable.getPageNumber())
                        .page_size(pageable.getPageSize())
                        .total_elements(claimsPage.getTotalElements())
                        .total_pages(claimsPage.getTotalPages())
                        .claims(claims)
                        .build()
        );
    }

    public ResponseEntity<IssueResponse> issue() {
        ecdhLinkService.issue();

        // todo!

        return ResponseEntity.ok(IssueResponse.builder()
                .uri("")
                .build());
    }

    @SneakyThrows
    public ResponseEntity<SuccessResponse> act(
            String epkSerialized,
            String ctxSerialized,
            String sigSerialized
    ) throws EcdhContextExpiredException {
        ecdhLinkService.act(epkSerialized, ctxSerialized, sigSerialized);

        // todo!

        return ResponseEntity.ok(SuccessResponse.builder().build());
    }

    private void assertPermission(boolean condition) {
        if (!condition) {
            throw new AccessDeniedException("Insufficient rights");
        }
    }
}

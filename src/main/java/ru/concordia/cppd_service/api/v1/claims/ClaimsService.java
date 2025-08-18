package ru.concordia.cppd_service.api.v1.claims;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concordia.cppd_service.api.v1.claims.model.*;
import ru.concordia.cppd_service.model.Claim;
import ru.concordia.cppd_service.repository.TemplateRepository;
import ru.concordia.cppd_service.service.NotificationService;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;
import ru.concordia.cppd_service.api.v1.claims.exceptions.ClaimNotFoundException;
import ru.concordia.cppd_service.repository.ClaimRepository;
import ru.concordia.cppd_service.service.EcdhLinkService;
import ru.concordia.cppd_service.api.v1.templates.exceptions.TemplateNotFoundException;
import ru.concordia.cppd_service.service.props.EcdhLinkProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimsService {
    private final ClaimRepository claimRepository;
    private final TemplateRepository templateRepository;

    private final EcdhLinkProperties ecdhLinkProperties;
    private final EcdhLinkService ecdhLinkService;

    private final NotificationService notificationService;

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

    @Transactional
    public ResponseEntity<IssueClaimResponse> issue(IssueClaimRequest payload, Long principalId, String principalEmail) {
        final var issuedClaims = new ArrayList<Claim>(payload.getCandidates_emails().size());
        final var template = templateRepository.findById(payload.getTemplate_id())
                .orElseThrow(TemplateNotFoundException::new);

        final var now = LocalDateTime.now();
        final var expiry = now.plusSeconds(ecdhLinkProperties.getExpiry());

        for (String candidateEmail : payload.getCandidates_emails()) {
            final var claim = Claim.builder()
                    .ownerId(principalId)
                    .ownerEmail(principalEmail)
                    .candidateEmail(candidateEmail)
                    .template(template)
                    .status(Claim.ClaimStatus.STATUS_QUEUED)
                    .expiresAt(expiry)
                    .build();

            issuedClaims.add(claim);
        }

        claimRepository.saveAll(issuedClaims);

        for (Claim claim : issuedClaims) {
            // noinspection HttpUrlsUsage
            final var candidateUri = String.format("http://%s/%s",
                    ecdhLinkProperties.getDomain(), ecdhLinkService.issue(claim.getId(), now, expiry));

            final var candidateNotification = notificationService.createCandidateNotification(
                    claim.getCandidateEmail(),
                    principalEmail,
                    template.getSubject(),
                    template.getContent() + candidateUri // for now.
            );

            notificationService.sendCandidateNotification(candidateNotification);
        }

        return ResponseEntity.ok(IssueClaimResponse.builder()
                .claims(issuedClaims)
                .build());
    }

    @SneakyThrows
    public ResponseEntity<ValidationResponse> validate(ValidationRequest payload) throws EcdhContextExpiredException {
        final var claims = ecdhLinkService.validate(payload.getEpk(), payload.getCtx(), payload.getSig());

        return ResponseEntity.ok(ValidationResponse.builder()
                .claim_id(Long.parseLong(claims.get("cid")))
                .build());
    }

    public ResponseEntity<SuccessResponse> act(ActClaimRequest payload) {
        final var claim = claimRepository.findById(payload.getClaim_id())
                .orElseThrow(ClaimNotFoundException::new);

        claim.setStatus(payload.getState() == ActState.ACT_CONSENT ?
                Claim.ClaimStatus.STATUS_CONSENT : Claim.ClaimStatus.STATUS_REFUSED);

        claim.setCandidateLastName(payload.getLast_name());
        claim.setCandidateFirstName(payload.getFirst_name());
        claim.setCandidateMiddleName(payload.getMiddle_name());

        claimRepository.save(claim);

        return ResponseEntity.ok(SuccessResponse.builder().build());
    }

    private void assertPermission(boolean condition) {
        if (!condition) {
            throw new AccessDeniedException("Insufficient rights");
        }
    }
}

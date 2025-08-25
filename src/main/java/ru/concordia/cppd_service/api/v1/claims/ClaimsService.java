package ru.concordia.cppd_service.api.v1.claims;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concordia.cppd_service.api.v1.claims.exceptions.ClaimNotFoundException;
import ru.concordia.cppd_service.api.v1.claims.model.*;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;
import ru.concordia.cppd_service.api.v1.templates.exceptions.TemplateNotFoundException;
import ru.concordia.cppd_service.model.Claim;
import ru.concordia.cppd_service.repository.ClaimRepository;
import ru.concordia.cppd_service.repository.TemplateRepository;
import ru.concordia.cppd_service.service.EcdhLinkService;
import ru.concordia.cppd_service.service.NotificationService;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.service.props.EcdhLinkProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
                        .claims(claims.stream().map(this::claimToRecord).toList())
                        .build()
        );
    }

    public ResponseEntity<ClaimResponse> scalar(UUID id, UUID principalId, String principalRole) {
        final var claim = claimRepository.findById(id)
                .orElseThrow(ClaimNotFoundException::new);

        assertPermission(Objects.equals(claim.getOwnerId(), principalId)
                || Objects.equals(principalRole, "ROLE_ADMIN"));

        return ResponseEntity.ok(
                ClaimResponse.builder()
                        .claim(claimToRecord(claim))
                        .build()
        );
    }

    public ResponseEntity<ClaimsCollectionResponse> myCollection(UUID principalId, Pageable pageable) {
        final var claimsPage = claimRepository.findByOwnerId(principalId, pageable);
        final var claims = claimsPage.getContent();

        return ResponseEntity.ok(
                ClaimsCollectionResponse.builder()
                        .page_id(pageable.getPageNumber())
                        .page_size(pageable.getPageSize())
                        .total_elements(claimsPage.getTotalElements())
                        .total_pages(claimsPage.getTotalPages())
                        .claims(claims.stream().map(this::claimToRecord).toList())
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<IssueClaimResponse> issue(IssueClaimRequest payload, UUID principalId, String principalEmail) {
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
            final var candidateUri = String.format(" http://%s%s",
                    ecdhLinkProperties.getDomain(), ecdhLinkService.issue(claim.getId(), now, expiry));

            final var candidateNotification = notificationService.createCandidateNotification(
                    claim.getCandidateEmail(),
                    principalId,
                    template.getSubject(),
                    template.getContent() + candidateUri // for now.
            );

            notificationService.sendCandidateNotification(candidateNotification);
        }

        return ResponseEntity.ok(IssueClaimResponse.builder()
                .claims(issuedClaims.stream().map(this::claimToRecord).toList())
                .build());
    }

    @SneakyThrows
    public ResponseEntity<ValidationResponse> validate(ValidationRequest payload) {
        final var claims = ecdhLinkService.validate(payload.getEpk(), payload.getCtx(), payload.getSig());

        return ResponseEntity.ok(ValidationResponse.builder()
                .claim_id(UUID.fromString(claims.get("cid")))
                .build());
    }

    public ResponseEntity<SuccessResponse> act(ActClaimRequest payload) throws EcdhContextExpiredException {
        if (!ecdhLinkService.isActiveEcdhSig(payload.getClaim_id(), payload.getSig()))
            throw new EcdhContextExpiredException();

        final var claim = claimRepository.findById(payload.getClaim_id())
                .orElseThrow(ClaimNotFoundException::new);

        assertPermission(claim.getStatus() == Claim.ClaimStatus.STATUS_QUEUED
                || claim.getStatus() == Claim.ClaimStatus.STATUS_WAITING);

        claim.setStatus(payload.getState() == ActState.ACT_CONSENT ?
                Claim.ClaimStatus.STATUS_CONSENT : Claim.ClaimStatus.STATUS_REFUSED);

        claim.setCandidateLastName(payload.getLast_name());
        claim.setCandidateFirstName(payload.getFirst_name());
        claim.setCandidateMiddleName(payload.getMiddle_name());
        claim.setCandidatePhone(payload.getPhone());
        claim.setRespondedAt(LocalDateTime.now());

        claimRepository.save(claim);
        ecdhLinkService.revokeEcdhSig(payload.getClaim_id());

        // Отправляем уведомление менеджеру о решении кандидата
        final var subjectDecision = payload.getState() == ActState.ACT_CONSENT ? "СОГЛАСИЕ" : "ОТКАЗ";
        final var subject = String.format("%s | Ответ кандидата [%s]", subjectDecision, claim.getCandidateEmail());
        final var content = String.format(
                "Кандидат %s %s %s (%s) дал <b>%s</b> в подписи документа на обработку персональных данных.",
                payload.getLast_name(),
                payload.getFirst_name(),
                payload.getMiddle_name(),
                claim.getCandidateEmail(),
                subjectDecision.toLowerCase(Locale.of("ru"))
        );

        // Создаем и отправляем уведомление с использованием UUID владельца заявки
        final var notification = notificationService.createManagerNotification(
                claim.getOwnerId(),
                subject,
                content
        );
        notificationService.sendManagerNotification(notification);

        return ResponseEntity.ok(SuccessResponse.builder().build());
    }

    private ClaimRecord claimToRecord(Claim claim) {
        return ClaimRecord.builder()
                .id(claim.getId())
                .owner_id(claim.getOwnerId())
                .owner_email(claim.getOwnerEmail())
                .candidate_email(claim.getCandidateEmail())
                .candidate_last_name(claim.getCandidateLastName())
                .candidate_first_name(claim.getCandidateFirstName())
                .candidate_middle_name(claim.getCandidateMiddleName())
                .candidate_birthdate(claim.getCandidateBirthdate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .candidate_phone(claim.getCandidatePhone())
                .template_id(claim.getTemplate().getId())
                .status(claim.getStatus())
                .responded_at(claim.getRespondedAt())
                .expires_at(claim.getExpiresAt())
                .created_at(claim.getCreatedAt())
                .updated_at(claim.getUpdatedAt())
                .build();
    }

    private void assertPermission(boolean condition) {
        if (!condition)
            throw new AccessDeniedException("Insufficient rights");
    }
}

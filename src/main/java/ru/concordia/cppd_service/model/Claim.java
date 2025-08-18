package ru.concordia.cppd_service.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "claims")
public class Claim {
    // При каждом запросе на сервис СОПДа будут передаваться след. заголовки:
    // X-Account-ID: 1
    // X-Account-Email: admin@concordia.t1.ru
    // X-Account-Role: ROLE_ADMIN или ROLE_MANAGER
    // X-Account-Telegram: @abc123
    // X-Account-Whatsapp: @abc123
    // X-Account-SMTP-Key: <Base64url encoded encrypted SMTP-key>

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "owner_email", nullable = false)
    private String ownerEmail;

    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;

    // Данные кандидата, если он дал согласие
    @Column(name = "candidate_last_name")
    private String candidateLastName;

    @Column(name = "candidate_first_name")
    private String candidateFirstName;

    @Column(name = "candidate_middle_name")
    private String candidateMiddleName;

    @Column(name = "candidate_phone")
    private String candidatePhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Nullable
    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    public enum ClaimStatus {
        STATUS_QUEUED, STATUS_WAITING, STATUS_CONSENT, STATUS_REFUSED, STATUS_TIMEOUT
    }
}

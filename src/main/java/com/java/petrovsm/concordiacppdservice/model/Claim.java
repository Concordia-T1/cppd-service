package com.java.petrovsm.concordiacppdservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(nullable = false)
    private String managerEmail;

    // ID кандидата из сервиса учетных данных (если он уже дал согласие)
    @Column
    private Long candidateId;

    // Данные кандидата, если он дал согласие
    @Column
    private String candidateFullName;

    @Column
    private String candidatePhone;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    @Column
    private LocalDateTime respondedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ClaimStatus {
        QUEUED, WAITING, CONSENT, REFUSED, TIMEOUT
    }
}

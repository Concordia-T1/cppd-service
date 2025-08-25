package ru.concordia.cppd_service.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.concordia.cppd_service.model.strategy.UUIDv7Strategy;

import java.time.LocalDateTime;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cppd")
@EntityListeners(AuditingEntityListener.class)
public class Cppd {
    @Id
    @UuidGenerator(algorithm = UUIDv7Strategy.class)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Nullable
    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}

package ru.concordia.cppd_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.concordia.cppd_service.model.Claim;
import ru.concordia.cppd_service.model.Claim.ClaimStatus;

import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Page<Claim> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Claim> findByOwnerIdAndStatus(UUID ownerId, ClaimStatus status, Pageable pageable);

    boolean existsByCandidateEmailAndStatus(String email, ClaimStatus status);
}

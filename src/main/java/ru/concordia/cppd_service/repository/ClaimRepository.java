package ru.concordia.cppd_service.repository;

import ru.concordia.cppd_service.model.Claim;
import ru.concordia.cppd_service.model.Claim.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Page<Claim> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Claim> findByOwnerIdAndStatus(Long ownerId, ClaimStatus status, Pageable pageable);

    boolean existsByCandidateEmailAndStatus(String email, ClaimStatus status);
}

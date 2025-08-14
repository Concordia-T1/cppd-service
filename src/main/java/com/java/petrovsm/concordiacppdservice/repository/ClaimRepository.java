package com.java.petrovsm.concordiacppdservice.repository;

import com.java.petrovsm.concordiacppdservice.model.Claim;
import com.java.petrovsm.concordiacppdservice.model.Claim.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByToken(String token);

    boolean existsByTokenAndStatusNot(String token, ClaimStatus status);

    boolean existsByCandidateEmailAndStatus(String email, ClaimStatus status);

    Page<Claim> findByManagerEmail(String managerEmail, Pageable pageable);

    Page<Claim> findByManagerEmailAndStatus(String managerEmail, ClaimStatus status, Pageable pageable);

    List<Claim> findByStatusAndExpiresAtLessThan(ClaimStatus status, java.time.LocalDateTime expiresAt);
}

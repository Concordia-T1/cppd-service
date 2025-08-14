package com.java.petrovsm.concordiacppdservice.repository;

import com.java.petrovsm.concordiacppdservice.model.CPPD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CPPDRepository extends JpaRepository<CPPD, Long> {
    Optional<CPPD> findByActive(boolean active);
}

package ru.concordia.cppd_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.concordia.cppd_service.model.Cppd;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CppdRepository extends JpaRepository<Cppd, UUID> {
    Optional<Cppd> findFirstByOrderByCreatedAtDesc();
}

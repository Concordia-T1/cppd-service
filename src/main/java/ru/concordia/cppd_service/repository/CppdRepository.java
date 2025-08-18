package ru.concordia.cppd_service.repository;

import ru.concordia.cppd_service.model.Cppd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CppdRepository extends JpaRepository<Cppd, Long> {
}

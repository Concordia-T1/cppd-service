package ru.concordia.cppd_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.concordia.cppd_service.model.Template;

import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<Template, UUID> {
    Page<Template> findByOwnerId(UUID UUID, Pageable pageable);
}

package ru.concordia.cppd_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.concordia.cppd_service.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    Page<Template> findByOwnerId(Long ownerId, Pageable pageable);
}

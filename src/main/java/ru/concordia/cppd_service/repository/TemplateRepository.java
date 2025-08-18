package ru.concordia.cppd_service.repository;

import ru.concordia.cppd_service.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByOwnerId(Long ownerId);
}

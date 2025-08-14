package com.java.petrovsm.concordiacppdservice.repository;

import com.java.petrovsm.concordiacppdservice.model.Template;
import com.java.petrovsm.concordiacppdservice.model.Template.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByType(TemplateType type);
    Optional<Template> findByTypeAndActive(TemplateType type, boolean active);
}

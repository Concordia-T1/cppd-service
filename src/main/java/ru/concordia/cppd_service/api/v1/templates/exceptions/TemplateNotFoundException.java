package ru.concordia.cppd_service.api.v1.templates.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class TemplateNotFoundException extends EntityNotFoundException {
    public TemplateNotFoundException() {
        super("Requested template not found");
    }
}

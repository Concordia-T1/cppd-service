package ru.concordia.cppd_service.api.v1.claims.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class ClaimNotFoundException extends EntityNotFoundException {
    public ClaimNotFoundException() {
        super("Requested claim not found");
    }
}

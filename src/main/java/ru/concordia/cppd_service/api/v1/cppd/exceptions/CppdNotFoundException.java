package ru.concordia.cppd_service.api.v1.cppd.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class CppdNotFoundException extends EntityNotFoundException {
    public CppdNotFoundException() {
        super("Requested cppd not found");
    }
}

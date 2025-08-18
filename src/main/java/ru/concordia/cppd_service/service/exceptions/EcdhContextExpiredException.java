package ru.concordia.cppd_service.service.exceptions;

public class EcdhContextExpiredException extends Exception {
    public EcdhContextExpiredException() {
        super("ECDH context expired");
    }
}

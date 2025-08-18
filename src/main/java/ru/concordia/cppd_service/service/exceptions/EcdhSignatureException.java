package ru.concordia.cppd_service.service.exceptions;

public class EcdhSignatureException extends Exception {
    public EcdhSignatureException() {
        super("ECDH signature mismatch");
    }
}

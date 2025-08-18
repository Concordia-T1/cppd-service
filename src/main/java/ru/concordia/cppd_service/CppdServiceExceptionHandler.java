package ru.concordia.cppd_service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.api.v1.model.ErrorResponse;
import ru.concordia.cppd_service.api.v1.model.ValidationError;
import ru.concordia.cppd_service.service.exceptions.EcdhSignatureException;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class CppdServiceExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(final BadCredentialsException e) {
        return CppdServiceException.BAD_CREDENTIALS.issueResponseEntity(e);
    }

    @ExceptionHandler({EcdhContextExpiredException.class, EcdhSignatureException.class})
    public ResponseEntity<ErrorResponse> handleEcdhException(final EcdhContextExpiredException e) {
        return CppdServiceException.INVALID_TOKEN.issueResponseEntity(e);
    }

    @ExceptionHandler({UsernameNotFoundException.class, AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final Exception e) {
        return CppdServiceException.ACCESS_DENIED.issueResponseEntity(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(final EntityNotFoundException e) {
        return CppdServiceException.ITEM_NOT_FOUND.issueResponseEntity(e);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(final NoResourceFoundException e) {
        return CppdServiceException.NOT_FOUND.issueResponseEntity(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final var errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(x -> ValidationError.builder()
                        .field(x.getField())
                        .detail(x.getDefaultMessage())
                        .build()
                ).toList();

        return CppdServiceException.BAD_REQUEST.issueValidationResponseEntity(errors);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(final TypeMismatchException e) {
        final var errors = Collections.singletonList(
                ValidationError.builder()
                        .field(e.getPropertyName())
                        .detail(e.getMessage())
                        .build());

        return CppdServiceException.BAD_REQUEST.issueValidationResponseEntity(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        final var errors = Collections.singletonList(
                ValidationError.builder()
                        .field("unk")
                        .detail(e.getMessage())
                        .build());

        return CppdServiceException.BAD_REQUEST.issueValidationResponseEntity(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return CppdServiceException.BAD_REQUEST_METHOD.issueResponseEntity(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .detail(String.format("INTERNAL_SERVER_ERROR: %s", e.getMessage()))
                        .build());
    }
}

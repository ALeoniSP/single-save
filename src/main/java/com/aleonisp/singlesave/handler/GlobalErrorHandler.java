package com.aleonisp.singlesave.handler;


import com.aleonisp.singlesave.dto.ErrorResponse;
import com.aleonisp.singlesave.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(
                        ex.getStatus().value(),
                        ex.getReasonCode(),
                        ex.getMessage(),
                        exchange.getRequest().getPath().value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(ServerWebInputException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        status.value(),
                        "BAD_REQUEST",
                        "Invalid request body",
                        exchange.getRequest().getPath().value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        status.value(),
                        "INTERNAL_ERROR",
                        "Unexpected error",
                        exchange.getRequest().getPath().value(),
                        Instant.now()
                ));
    }
}
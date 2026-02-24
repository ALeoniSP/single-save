package com.aleonisp.singlesave.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException {
    private final String reasonCode;
    private final HttpStatus status;

    public DomainException(String reasonCode, HttpStatus status, String message) {
        super(message);
        this.reasonCode = reasonCode;
        this.status = status;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
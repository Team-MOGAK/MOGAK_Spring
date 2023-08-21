package com.mogak.spring.global;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    protected BaseException(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}

package com.mogak.spring.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final LocalDateTime now = LocalDateTime.now();
    private int status;
    private String errorCode;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode e) {
        return new ErrorResponse(
                e.getStatus().value(),
                e.name(),
                e.getMessage());
    }
}

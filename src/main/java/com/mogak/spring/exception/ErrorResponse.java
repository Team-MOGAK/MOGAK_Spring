package com.mogak.spring.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mogak.spring.global.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"time", "status", "code", "message", "result"})
public class ErrorResponse {
    private final LocalDateTime time = LocalDateTime.now();
    private int status;
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode e) {
        return new ErrorResponse(
                e.getStatus().value(),
                e.getCode(),
                e.getMessage());
    }
}

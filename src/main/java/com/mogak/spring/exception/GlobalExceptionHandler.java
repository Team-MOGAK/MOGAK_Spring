package com.mogak.spring.exception;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        if (e.getHttpStatus().is5xxServerError()) {
            log.error("BaseException occurred: code={}, message={}", e.getCode(), e.getMessage(), e);
        } else {
            log.warn("BaseException occurred: code={}, message={}", e.getCode(), e.getMessage());
        }
        ErrorCode errorCode = ErrorCode.findByMessage(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.of(Objects.requireNonNull(errorCode)));
    }

    /**
     * HTTP Request Method 오류 전용
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(ErrorCode.NOT_SUPPORTED_METHOD_ERROR));
    }

    /**
     * 잘못 입력된 경우
     * */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.INVALID_PARAMETER_ERROR));
    }

    /**
     * 잘못 입력된 경우
     * */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.warn("NullPointerException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
    }

    /**
     * 잘못 입력된 경우
     * */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause() == null ? e.getMessage() : e.getMostSpecificCause().getMessage();
        if (message != null && message.toLowerCase().contains("uq_post_active_daily_jogak")) {
            return ResponseEntity.status(ErrorCode.ALREADY_EXISTS_POST.getStatus())
                    .body(ErrorResponse.of(ErrorCode.ALREADY_EXISTS_POST));
        }
        log.error("Data integrity violation occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * 내부 서버 오류 전용
     * */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(RuntimeException e) {
        log.error("Unexpected runtime exception occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}

package com.mogak.spring.exception;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> BaseException(BaseException e) {
        e.printStackTrace();
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
    @ExceptionHandler({NullPointerException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(NullPointerException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
    }

    /**
     * 내부 서버 오류 전용
     * */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(RuntimeException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}

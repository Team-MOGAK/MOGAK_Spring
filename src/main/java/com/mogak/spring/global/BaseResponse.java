package com.mogak.spring.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.mogak.spring.global.ErrorCode.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"time", "status", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();
    private final HttpStatus status;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.status = SUCCESS.getStatus();
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(ErrorCode code) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
    }
    
}

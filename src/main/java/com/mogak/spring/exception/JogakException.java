package com.mogak.spring.exception;

import org.springframework.http.HttpStatus;

public class JogakException extends BaseException {

    public JogakException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}
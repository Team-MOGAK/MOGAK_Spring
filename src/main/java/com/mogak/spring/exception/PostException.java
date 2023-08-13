package com.mogak.spring.exception;

import org.springframework.http.HttpStatus;

public class PostException extends BaseException {
    public PostException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

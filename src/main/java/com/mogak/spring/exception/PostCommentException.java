package com.mogak.spring.exception;

import org.springframework.http.HttpStatus;

public class PostCommentException extends BaseException {
    public PostCommentException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

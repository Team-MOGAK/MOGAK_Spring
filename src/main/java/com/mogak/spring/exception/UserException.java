package com.mogak.spring.exception;

public class UserException extends BaseException {
    public UserException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

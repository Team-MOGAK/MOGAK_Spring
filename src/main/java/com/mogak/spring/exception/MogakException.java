package com.mogak.spring.exception;

public class MogakException extends BaseException {
    public MogakException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

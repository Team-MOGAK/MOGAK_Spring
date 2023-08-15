package com.mogak.spring.exception;

public class CommonException extends BaseException {

    public CommonException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

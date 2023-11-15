package com.mogak.spring.exception;

import com.mogak.spring.global.ErrorCode;

public class BaseException extends com.mogak.spring.global.BaseException {

    public BaseException(ErrorCode e) {
        super(e.getStatus(), e.getCode(), e.getMessage());
    }
}

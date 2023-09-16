package com.mogak.spring.exception;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;

public class MogakException extends BaseException {
    public MogakException(ErrorCode e) {
        super(e.getStatus(), e.getCode(), e.getMessage());
    }
}

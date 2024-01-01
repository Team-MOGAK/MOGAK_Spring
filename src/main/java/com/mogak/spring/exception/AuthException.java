package com.mogak.spring.exception;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;

public class AuthException extends BaseException {
    public AuthException(ErrorCode e) {
        super(e.getStatus(), e.getCode(), e.getMessage());
    }

}

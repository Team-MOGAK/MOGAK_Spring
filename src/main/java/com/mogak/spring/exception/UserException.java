package com.mogak.spring.exception;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;

public class UserException extends BaseException {
    public UserException(ErrorCode e) {
        super(e.getStatus(), e.getMessage());
    }
}

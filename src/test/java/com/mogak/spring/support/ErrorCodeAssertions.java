package com.mogak.spring.support;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

public final class ErrorCodeAssertions {

    private ErrorCodeAssertions() {
    }

    public static void assertErrorCode(Throwable throwable, ErrorCode errorCode) {
        assertThat(throwable)
                .isInstanceOf(BaseException.class);
        BaseException baseException = (BaseException) throwable;
        assertThat(baseException.getHttpStatus()).isEqualTo(errorCode.getStatus());
        assertThat(baseException.getCode()).isEqualTo(errorCode.getCode());
        assertThat(baseException.getMessage()).isEqualTo(errorCode.getMessage());
    }
}

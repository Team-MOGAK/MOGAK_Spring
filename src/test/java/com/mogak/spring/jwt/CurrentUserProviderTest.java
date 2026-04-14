package com.mogak.spring.jwt;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.catchThrowable;

class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증 사용자가 없으면 EMPTY_TOKEN 예외를 던진다")
    void requireCurrentUserThrowsEmptyTokenWhenAuthenticationMissing() {
        Throwable throwable = catchThrowable(currentUserProvider::requireCurrentUser);

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EMPTY_TOKEN);
    }
}

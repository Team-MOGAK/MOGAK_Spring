package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.CurrentUserProvider;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.DailyJogakRepository;
import com.mogak.spring.repository.JogakPeriodRepository;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.ModaratRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ModaratRepository modaratRepository;
    @Mock private MogakRepository mogakRepository;
    @Mock private JogakRepository jogakRepository;
    @Mock private DailyJogakRepository dailyJogakRepository;
    @Mock private JogakPeriodRepository jogakPeriodRepository;
    @Mock private AppleOAuthUserProvider appleOAuthUserProvider;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Spy private CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("기존 사용자가 애플 로그인하면 발급한 refresh 토큰을 사용자 DB에 저장한다")
    void appleLoginStoresRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        AppleLoginRequest request = AppleLoginRequest.builder()
                .id_token("apple-id-token")
                .build();

        when(appleOAuthUserProvider.getAppleUser("apple-id-token")).thenReturn(new AppleUserResponse("user@test.com"));
        when(userRepository.existsByEmail("user@test.com")).thenReturn(true);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createAccessToken(1L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken("user@test.com")).thenReturn("refresh-token");

        AppleLoginResponse response = authService.appleLogin(request);

        assertThat(response.getTokens().getRefreshToken()).isEqualTo("refresh-token");
        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("DB에 저장된 refresh 토큰과 다르면 재발급을 거부한다")
    void reissueThrowsWhenRefreshTokenDoesNotMatchStoredValue() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");

        when(jwtTokenProvider.getEmailByRefresh("presented-refresh-token")).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        Throwable throwable = catchThrowable(() -> authService.reissue("presented-refresh-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("refresh 토큰을 재발급하면 새 refresh 토큰을 사용자 DB에 저장한다")
    void reissueStoresRotatedRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");

        when(jwtTokenProvider.getEmailByRefresh("stored-refresh-token")).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.refresh("stored-refresh-token", 1L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn(
                JwtTokens.builder()
                        .accessToken("new-access-token")
                        .refreshToken("rotated-refresh-token")
                        .build()
        );

        JwtTokens result = authService.reissue("stored-refresh-token");

        assertThat(result.getRefreshToken()).isEqualTo("rotated-refresh-token");
        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isEqualTo("rotated-refresh-token");
    }

    @Test
    @DisplayName("로그아웃하면 사용자 DB에 저장된 refresh 토큰을 제거한다")
    void logoutClearsStoredRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        authService.logout(1L);

        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isNull();
    }
}

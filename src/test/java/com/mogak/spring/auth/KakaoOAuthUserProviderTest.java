package com.mogak.spring.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.user.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KakaoOAuthUserProviderTest {

    private final KakaoClient kakaoClient = mock(KakaoClient.class);
    private final KakaoOAuthUserProvider provider = new KakaoOAuthUserProvider(kakaoClient);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Kakao user response에서 providerUserId, email, emailVerified를 추출한다")
    void extractsKakaoProfile() throws Exception {
        when(kakaoClient.getUser("Bearer kakao-access-token"))
                .thenReturn(kakaoResponse(true, true));

        SocialUserProfile profile = provider.getUser("kakao-access-token");

        assertThat(profile.provider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(profile.providerUserId()).isEqualTo("12345");
        assertThat(profile.email()).isEqualTo("kakao@test.com");
        assertThat(profile.emailVerified()).isTrue();
    }

    @Test
    @DisplayName("Kakao 이메일 유효 상태가 false면 검증되지 않은 이메일로 처리한다")
    void treatsInvalidEmailAsUnverified() throws Exception {
        when(kakaoClient.getUser("Bearer kakao-access-token"))
                .thenReturn(kakaoResponse(false, true));

        SocialUserProfile profile = provider.getUser("kakao-access-token");

        assertThat(profile.emailVerified()).isFalse();
    }

    @Test
    @DisplayName("Kakao 이메일 검증 상태가 false면 검증되지 않은 이메일로 처리한다")
    void treatsUnverifiedEmailAsUnverified() throws Exception {
        when(kakaoClient.getUser("Bearer kakao-access-token"))
                .thenReturn(kakaoResponse(true, false));

        SocialUserProfile profile = provider.getUser("kakao-access-token");

        assertThat(profile.emailVerified()).isFalse();
    }

    @Test
    @DisplayName("Kakao 이메일이 없어도 providerUserId를 기반으로 프로필을 반환한다")
    void extractsKakaoProfileWithoutEmail() throws Exception {
        when(kakaoClient.getUser("Bearer kakao-access-token"))
                .thenReturn(kakaoResponseWithoutEmail());

        SocialUserProfile profile = provider.getUser("kakao-access-token");

        assertThat(profile.provider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(profile.providerUserId()).isEqualTo("12345");
        assertThat(profile.email()).isNull();
        assertThat(profile.emailVerified()).isFalse();
    }

    private KakaoUserResponse kakaoResponse(boolean emailValid, boolean emailVerified) throws Exception {
        String json = """
                {
                  "id": 12345,
                  "kakao_account": {
                    "email": "kakao@test.com",
                    "is_email_valid": %s,
                    "is_email_verified": %s
                  }
                }
                """.formatted(emailValid, emailVerified);
        return objectMapper.readValue(json, KakaoUserResponse.class);
    }

    private KakaoUserResponse kakaoResponseWithoutEmail() throws Exception {
        String json = """
                {
                  "id": 12345,
                  "kakao_account": {}
                }
                """;
        return objectMapper.readValue(json, KakaoUserResponse.class);
    }
}

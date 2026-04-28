package com.mogak.spring.service;

import com.mogak.spring.auth.SocialOAuthUserProvider;
import com.mogak.spring.auth.SocialUserProfile;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.SocialAccount;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.*;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.result.SocialLoginResult;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ModaratRepository modaratRepository;
    @Mock private MogakRepository mogakRepository;
    @Mock private JogakRepository jogakRepository;
    @Mock private DailyJogakRepository dailyJogakRepository;
    @Mock private JogakPeriodRepository jogakPeriodRepository;
    @Mock private PostRepository postRepository;
    @Mock private PostCommentRepository postCommentRepository;
    @Mock private PostImgRepository postImgRepository;
    @Mock private PostLikeRepository postLikeRepository;
    @Mock private FollowRepository followRepository;
    @Mock private SocialAccountRepository socialAccountRepository;
    @Mock private SocialOAuthUserProvider socialOAuthUserProvider;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private StorageCleanupService storageCleanupService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "socialOAuthUserProviders", List.of(socialOAuthUserProvider));
    }

    @Test
    @DisplayName("기존 사용자가 애플 로그인하면 발급한 refresh 토큰을 사용자 DB에 저장한다")
    void appleLoginStoresRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        SocialAccount socialAccount = SocialAccount.connect(user, SocialProvider.APPLE, "apple-sub", "user@test.com");

        when(socialOAuthUserProvider.supports(SocialProvider.APPLE)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("apple-id-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.APPLE, "apple-sub", "user@test.com", true));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.APPLE, "apple-sub"))
                .thenReturn(Optional.of(socialAccount));
        when(jwtTokenProvider.createAccessToken(1L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("refresh-token");

        SocialLoginResult response = authService.appleLogin("apple-id-token");

        assertThat(response.tokens().refreshToken()).isEqualTo("refresh-token");
        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("연결된 애플 계정은 id token에 이메일이 없어도 providerUserId 기준으로 로그인한다")
    void appleLoginUsesConnectedSocialAccountWithoutEmail() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        SocialAccount socialAccount = SocialAccount.connect(user, SocialProvider.APPLE, "apple-sub", "user@test.com");

        when(socialOAuthUserProvider.supports(SocialProvider.APPLE)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("apple-id-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.APPLE, "apple-sub", null, false));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.APPLE, "apple-sub"))
                .thenReturn(Optional.of(socialAccount));
        when(jwtTokenProvider.createAccessToken(1L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("refresh-token");

        SocialLoginResult response = authService.appleLogin("apple-id-token");

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.tokens().accessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("연결된 소셜 계정이 있으면 providerUserId 기준으로 로그인한다")
    void socialLoginUsesConnectedSocialAccount() {
        User user = TestFixtureFactory.user(1L, "google@test.com", "tester", null, null);
        SocialAccount socialAccount = SocialAccount.connect(user, SocialProvider.GOOGLE, "google-sub", "google@test.com");

        when(socialOAuthUserProvider.supports(SocialProvider.GOOGLE)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("google-id-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.GOOGLE, "google-sub", "google@test.com", true));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.of(socialAccount));
        when(jwtTokenProvider.createAccessToken(1L, "google@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("refresh-token");

        SocialLoginResult response = authService.socialLogin(SocialProvider.GOOGLE, "google-id-token");

        assertThat(response.isRegistered()).isTrue();
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.tokens().accessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("같은 이메일의 활성 사용자가 있으면 자동 연결하지 않고 계정 연결 필요 오류를 반환한다")
    void socialLoginRejectsExistingEmailUserWithoutLinkedSocialAccount() {
        User user = TestFixtureFactory.user(1L, "kakao@test.com", "tester", null, null);

        when(socialOAuthUserProvider.supports(SocialProvider.KAKAO)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("kakao-access-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.KAKAO, "kakao-id", "kakao@test.com", true));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.KAKAO, "kakao-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("kakao@test.com")).thenReturn(Optional.of(user));

        Throwable throwable = catchThrowable(() -> authService.socialLogin(SocialProvider.KAKAO, "kakao-access-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.SOCIAL_ACCOUNT_LINK_REQUIRED);
    }

    @Test
    @DisplayName("같은 이메일의 사용자가 없으면 새 사용자와 소셜 계정을 생성한다")
    void socialLoginCreatesNewUserAndSocialAccount() {
        User user = TestFixtureFactory.user(2L, "new-kakao@test.com", null, null, null);

        when(socialOAuthUserProvider.supports(SocialProvider.KAKAO)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("kakao-access-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.KAKAO, "kakao-id", "new-kakao@test.com", true));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.KAKAO, "kakao-id"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("new-kakao@test.com")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);
        when(jwtTokenProvider.createAccessToken(2L, "new-kakao@test.com", SecurityAuthority.PENDING.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(2L)).thenReturn("refresh-token");

        SocialLoginResult response = authService.socialLogin(SocialProvider.KAKAO, "kakao-access-token");

        assertThat(response.isRegistered()).isFalse();
        verify(socialAccountRepository).saveAndFlush(org.mockito.ArgumentMatchers.argThat(account ->
                account.getUser() == user &&
                        account.getProvider() == SocialProvider.KAKAO &&
                        "kakao-id".equals(account.getProviderUserId())
        ));
    }

    @Test
    @DisplayName("카카오 신규 사용자는 이메일이 없어도 PENDING 사용자와 소셜 계정을 생성한다")
    void socialLoginCreatesNewKakaoUserWithoutEmail() {
        User user = TestFixtureFactory.user(3L, null, null, null, null);

        when(socialOAuthUserProvider.supports(SocialProvider.KAKAO)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("kakao-access-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.KAKAO, "kakao-id", null, false));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.KAKAO, "kakao-id"))
                .thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);
        when(jwtTokenProvider.createAccessToken(3L, null, SecurityAuthority.PENDING.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(3L)).thenReturn("refresh-token");

        SocialLoginResult response = authService.socialLogin(SocialProvider.KAKAO, "kakao-access-token");

        assertThat(response.isRegistered()).isFalse();
        assertThat(response.tokens().refreshToken()).isEqualTo("refresh-token");
        verify(socialAccountRepository).saveAndFlush(org.mockito.ArgumentMatchers.argThat(account ->
                account.getUser() == user &&
                        account.getProvider() == SocialProvider.KAKAO &&
                        "kakao-id".equals(account.getProviderUserId()) &&
                        account.getEmail() == null
        ));
    }

    @Test
    @DisplayName("신규 소셜 로그인 이메일이 검증되지 않았으면 사용자를 생성하지 않는다")
    void socialLoginRejectsUnverifiedEmailForNewUser() {

        when(socialOAuthUserProvider.supports(SocialProvider.GOOGLE)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("google-id-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.GOOGLE, "google-sub", "new-google@test.com", false));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> authService.socialLogin(SocialProvider.GOOGLE, "google-id-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.SOCIAL_EMAIL_NOT_VERIFIED);
    }

    @Test
    @DisplayName("신규 사용자 저장 중 이메일 unique 제약이 발생하면 계정 연결 필요 오류를 반환한다")
    void socialLoginConvertsEmailUniqueRaceToLinkRequired() {

        when(socialOAuthUserProvider.supports(SocialProvider.GOOGLE)).thenReturn(true);
        when(socialOAuthUserProvider.getUser("google-id-token"))
                .thenReturn(new SocialUserProfile(SocialProvider.GOOGLE, "google-sub", "race@test.com", true));
        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("race@test.com")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        Throwable throwable = catchThrowable(() -> authService.socialLogin(SocialProvider.GOOGLE, "google-id-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.SOCIAL_ACCOUNT_LINK_REQUIRED);
    }

    @Test
    @DisplayName("DB에 저장된 refresh 토큰과 다르면 재발급을 거부한다")
    void reissueThrowsWhenRefreshTokenDoesNotMatchStoredValue() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");

        when(jwtTokenProvider.getUserIdByRefresh("presented-refresh-token")).thenReturn(1L);
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));

        Throwable throwable = catchThrowable(() -> authService.reissue("presented-refresh-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("refresh 토큰을 재발급하면 새 refresh 토큰을 사용자 DB에 저장한다")
    void reissueStoresRotatedRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");

        when(jwtTokenProvider.getUserIdByRefresh("stored-refresh-token")).thenReturn(1L);
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.refresh("stored-refresh-token", 1L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn(
                new JwtTokens("new-access-token", "rotated-refresh-token")
        );

        JwtTokens result = authService.reissue("stored-refresh-token");

        assertThat(result.refreshToken()).isEqualTo("rotated-refresh-token");
        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isEqualTo("rotated-refresh-token");
    }

    @Test
    @DisplayName("로그아웃하면 사용자 DB에 저장된 refresh 토큰을 제거한다")
    void logoutClearsStoredRefreshToken() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        ReflectionTestUtils.setField(user, "refreshToken", "stored-refresh-token");
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));

        authService.logout(1L);

        assertThat(ReflectionTestUtils.getField(user, "refreshToken")).isNull();
    }

    @Test
    @DisplayName("회원탈퇴는 사용자 id 기준으로 계정을 비활성화하고 관련 데이터를 제거한다")
    void deleteUserMarksInactiveAndDeletesRelatedData() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(11L, user, "모다라트", "#ffffff");
        Mogak mogak = TestFixtureFactory.mogak(21L, user, modarat, TestFixtureFactory.category(1, "대분류"), "모각", "#aaaaaa");
        Jogak jogak = TestFixtureFactory.jogak(31L, mogak, "조각", false, null, null, 0);

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(postImgRepository.findAllByPostOwnerId(1L)).thenReturn(List.of());
        when(postLikeRepository.findActiveAllByUserIdOnOtherUserPosts(1L)).thenReturn(List.of());
        when(postCommentRepository.findActiveAllByPostOwnerId(1L)).thenReturn(List.of());
        when(postCommentRepository.findActiveAllByUserId(1L)).thenReturn(List.of());
        when(postRepository.findActiveAllByUserId(1L)).thenReturn(List.of());
        when(jogakRepository.findAllByUserId(1L)).thenReturn(Optional.of(List.of(jogak)));
        when(dailyJogakRepository.findActiveAllByJogak(jogak)).thenReturn(List.of());
        when(mogakRepository.findAllByUser(user)).thenReturn(List.of(mogak));
        when(modaratRepository.findModaratsByUserId(1L)).thenReturn(List.of(modarat));

        authService.deleteUser(1L);

        assertThat(user.isDeleted()).isTrue();
        assertThat(jogak.isDeleted()).isTrue();
        assertThat(mogak.isDeleted()).isTrue();
        assertThat(modarat.isDeleted()).isTrue();
        org.mockito.Mockito.verify(jogakPeriodRepository).deleteAllByJogakId(31L);
    }

    @Test
    @DisplayName("회원탈퇴는 다른 사용자의 게시글에 남긴 댓글과 좋아요 카운터를 함께 감소시킨다")
    void deleteUserUpdatesCountersOnOtherUsersPosts() {
        User withdrawingUser = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        User postOwner = TestFixtureFactory.user(2L, "owner@test.com", "owner", null, null);
        Mogak ownerMogak = TestFixtureFactory.mogak(21L, postOwner,
                TestFixtureFactory.modarat(11L, postOwner, "모다라트", "#ffffff"),
                TestFixtureFactory.category(1, "대분류"), "모각", "#aaaaaa");
        Jogak ownerJogak = TestFixtureFactory.jogak(31L, ownerMogak, "조각", false, java.time.LocalDate.now(), null, 0);
        Post post = Post.builder()
                .id(41L)
                .dailyJogak(TestFixtureFactory.dailyJogak(51L, ownerJogak, false))
                .user(postOwner)
                .contents("content")
                .postThumbnailUrl("https://example.com/thumb.png")
                .viewCnt(0)
                .likeCnt(1)
                .commentCnt(1)
                .build();
        PostComment comment = PostComment.builder()
                .id(61L)
                .post(post)
                .user(withdrawingUser)
                .contents("comment")
                .build();
        PostLike like = PostLike.builder()
                .id(71L)
                .post(post)
                .user(withdrawingUser)
                .build();

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(withdrawingUser));
        when(postImgRepository.findAllByPostOwnerId(1L)).thenReturn(List.of());
        when(postLikeRepository.findActiveAllByUserIdOnOtherUserPosts(1L)).thenReturn(List.of(like));
        when(postCommentRepository.findActiveAllByPostOwnerId(1L)).thenReturn(List.of());
        when(postCommentRepository.findActiveAllByUserId(1L)).thenReturn(List.of(comment));
        when(postRepository.findActiveAllByUserId(1L)).thenReturn(List.of());
        when(jogakRepository.findAllByUserId(1L)).thenReturn(Optional.empty());
        when(mogakRepository.findAllByUser(withdrawingUser)).thenReturn(List.of());
        when(modaratRepository.findModaratsByUserId(1L)).thenReturn(List.of());

        authService.deleteUser(1L);

        assertThat(comment.isDeleted()).isTrue();
        assertThat(post.getCommentCnt()).isZero();
        assertThat(post.getLikeCnt()).isZero();
        verify(postLikeRepository).deleteAllRelatedToUser(1L);
        verify(followRepository).deleteAllRelatedToUser(1L);
    }
}

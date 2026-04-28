package com.mogak.spring.service;

import com.mogak.spring.auth.SocialOAuthUserProvider;
import com.mogak.spring.auth.SocialUserProfile;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.SocialAccount;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.*;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.result.SocialLoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    //로그인, 토큰, 로그아웃, 회원탈퇴

    private final UserRepository userRepository;
    private final ModaratRepository modaratRepository;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;
    private final DailyJogakRepository dailyJogakRepository;
    private final JogakPeriodRepository jogakPeriodRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final FollowRepository followRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final List<SocialOAuthUserProvider> socialOAuthUserProviders;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageCleanupService storageCleanupService;
    private static final String DIR_NAME = "img";

    //로그인
    @Transactional
    public SocialLoginResult appleLogin(String idToken) {
        return login(SocialProvider.APPLE, idToken);
    }

    @Transactional
    public SocialLoginResult socialLogin(SocialProvider provider, String token) {
        return login(provider, token);
    }

    private SocialLoginResult login(SocialProvider provider, String token) {
        SocialUserProfile profile = resolveSocialUser(provider, token);
        User user = resolveUser(profile);
        JwtTokens jwtTokens = issueTokens(user);
        return new SocialLoginResult(isRegisterNickname(user), user.getId(), jwtTokens);
    }

    private SocialUserProfile resolveSocialUser(SocialProvider provider, String token) {
        if (token == null || token.isBlank()) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }
        SocialUserProfile profile = socialOAuthUserProviders.stream()
                .filter(candidate -> candidate.supports(provider))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER))
                .getUser(token);
        if (profile.provider() != provider) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }
        return profile;
    }

    private User resolveUser(SocialUserProfile profile) {
        validateProviderUserId(profile);
        Optional<SocialAccount> existingAccount =
                socialAccountRepository.findByProviderAndProviderUserId(profile.provider(), profile.providerUserId());
        if (existingAccount.isPresent()) {
            User user = existingAccount.get().getUser();
            if (user.isDeleted()) {
                throw new UserException(ErrorCode.NOT_EXIST_USER);
            }
            return user;
        }

        validateEmailForNewAccount(profile);
        if (hasEmail(profile)) {
            Optional<User> existingUser = userRepository.findByEmail(profile.email());
            if (existingUser.isPresent() && existingUser.get().isDeleted()) {
                throw new UserException(ErrorCode.NOT_EXIST_USER);
            }
            if (existingUser.isPresent()) {
                throw new BaseException(ErrorCode.SOCIAL_ACCOUNT_LINK_REQUIRED);
            }
        }
        User user = createSocialUser(profile);
        if (socialAccountRepository.existsByUserAndProvider(user, profile.provider())) {
            throw new BaseException(ErrorCode.SOCIAL_ACCOUNT_CONFLICT);
        }
        connectSocialAccount(user, profile);
        return user;
    }

    private User createSocialUser(SocialUserProfile profile) {
        try {
            return userRepository.saveAndFlush(new User(profile.email()));
        } catch (DataIntegrityViolationException e) {
            throw new BaseException(ErrorCode.SOCIAL_ACCOUNT_LINK_REQUIRED);
        }
    }

    private void connectSocialAccount(User user, SocialUserProfile profile) {
        try {
            socialAccountRepository.saveAndFlush(
                    SocialAccount.connect(user, profile.provider(), profile.providerUserId(), profile.email())
            );
        } catch (DataIntegrityViolationException e) {
            throw new BaseException(ErrorCode.SOCIAL_ACCOUNT_CONFLICT);
        }
    }

    private void validateProviderUserId(SocialUserProfile profile) {
        if (profile.providerUserId() == null || profile.providerUserId().isBlank()) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    private void validateEmailForNewAccount(SocialUserProfile profile) {
        if (!hasEmail(profile)) {
            if (profile.provider() == SocialProvider.KAKAO) {
                return;
            }
            throw new BaseException(ErrorCode.SOCIAL_EMAIL_REQUIRED);
        }
        if (!Boolean.TRUE.equals(profile.emailVerified())) {
            throw new BaseException(ErrorCode.SOCIAL_EMAIL_NOT_VERIFIED);
        }
    }

    private boolean hasEmail(SocialUserProfile profile) {
        return profile.email() != null && !profile.email().isBlank();
    }

    /**
     * 닉네임 등록 여부
     */
    private boolean isRegisterNickname(User user) {
        return user.getNickname() != null && !user.getNickname().isEmpty();
    }

    /**
     * 토큰 발급
     */
    private JwtTokens issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), resolveTokenRole(user));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);
        return new JwtTokens(accessToken, refreshToken);

    }

    @Transactional
    public JwtTokens reissue(String refreshToken) {
        Long userId = jwtTokenProvider.getUserIdByRefresh(refreshToken);
        User findUser = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        validateStoredRefreshToken(findUser, refreshToken);

        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, findUser.getId(), findUser.getEmail(), resolveTokenRole(findUser));
        findUser.updateRefreshToken(jwtTokens.refreshToken());
        return jwtTokens;
    }


    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        user.clearRefreshToken();
    }

    /**
     * 로그인한 사용자 탈퇴
     */
    @Transactional
    public void deleteUser(Long userId) {
        User deleteUser = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        deleteUserInfo(deleteUser);
    }

    public void deleteUserInfo(User deleteUser) {
        Long userId = deleteUser.getId();
        List<PostImg> ownedPostImages = postImgRepository.findAllByPostOwnerId(userId);
        if (!ownedPostImages.isEmpty()) {
            storageCleanupService.deletePostImagesAfterCommit(ownedPostImages, DIR_NAME);
            postImgRepository.deleteAllByPostOwnerId(userId);
        }
        postLikeRepository.findActiveAllByUserIdOnOtherUserPosts(userId).stream()
                .map(PostLike::getPost)
                .forEach(Post::subtractPostLike);
        postLikeRepository.deleteAllRelatedToUser(userId);
        followRepository.deleteAllRelatedToUser(userId);

        postCommentRepository.findActiveAllByPostOwnerId(userId).forEach(this::deleteComment);
        postCommentRepository.findActiveAllByUserId(userId).forEach(comment -> {
            if (comment.isDeleted()) {
                return;
            }
            Post post = comment.getPost();
            deleteComment(comment);
            if (!Objects.equals(post.getUser().getId(), userId) && !post.isDeleted()) {
                post.subtractCommentCnt();
            }
        });
        postRepository.findActiveAllByUserId(userId).forEach(Post::delete);

        Optional<List<Jogak>> optJogaks = jogakRepository.findAllByUserId(userId);
        if (optJogaks.isPresent()) {
            for (Jogak jogak : optJogaks.get()) {
                dailyJogakRepository.findActiveAllByJogak(jogak).forEach(dailyJogak -> dailyJogak.delete());
                jogakPeriodRepository.deleteAllByJogakId(jogak.getId());
                jogak.delete();
            }
        }
        mogakRepository.findAllByUser(deleteUser).forEach(Mogak::delete);
        modaratRepository.findModaratsByUserId(userId).forEach(Modarat::delete);
        deleteUser.clearRefreshToken();
        deleteUser.delete();
    }

    private void deleteComment(PostComment comment) {
        if (!comment.isDeleted()) {
            comment.delete();
        }
    }

    private void validateStoredRefreshToken(User user, String refreshToken) {
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new BaseException(ErrorCode.WRONG_TOKEN);
        }
    }

    private String resolveTokenRole(User user) {
        if (isRegisterNickname(user) && user.getRole() != null) {
            return user.getRole().getKey();
        }
        return SecurityAuthority.PENDING.getAuthority();
    }
}

package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.*;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import com.mogak.spring.web.dto.authdto.AuthResponse;
import lombok.RequiredArgsConstructor;
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
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageCleanupService storageCleanupService;
    private static final String DIR_NAME = "img";

    //로그인
    @Transactional
    public AppleLoginResponse appleLogin(AppleLoginRequest request) {
        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.idToken());
        Optional<User> existingUser = userRepository.findByEmail(appleUser.email());
        if (existingUser.isPresent() && existingUser.get().isDeleted()) {
            throw new UserException(ErrorCode.NOT_EXIST_USER);
        }
        boolean isRegistered = existingUser.isPresent();
        if (isRegistered) { //회원가입이 되어 있는 경우-이메일 체크
            User findUser = existingUser.orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
            JwtTokens jwtTokens = issueTokens(findUser); //토큰 발급
            if (!isRegisterNickname(findUser)) { //해당 이메일로 가입한 유저의 닉네임 없으면 회원가입하도록
                return new AppleLoginResponse(false, findUser.getId(), jwtTokens);
            }
            return new AppleLoginResponse(true, findUser.getId(), jwtTokens);
        }
        //회원가입이 되어 있지 않은 경우
        User oauthUser = new User(appleUser.email());
        User savedUser = userRepository.save(oauthUser);
        JwtTokens jwtTokens = issueTokens(savedUser);
        return new AppleLoginResponse(false, savedUser.getId(), jwtTokens);
    }

    /**
     * 닉네임 등록 여부
     */
    private boolean isRegisterNickname(User user) {
        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 토큰 발급
     */
    private JwtTokens issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), resolveTokenRole(user));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        return new JwtTokens(accessToken, refreshToken);

    }

    @Transactional
    public JwtTokens reissue(String refreshToken) {
        String email = jwtTokenProvider.getEmailByRefresh(refreshToken);
        User findUser = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        validateStoredRefreshToken(findUser, refreshToken);

        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, findUser.getId(), email, resolveTokenRole(findUser));
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
    public AuthResponse.WithdrawDto deleteUser(Long userId) {
        User deleteUser = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        deleteUserInfo(deleteUser);
        return new AuthResponse.WithdrawDto(true);
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

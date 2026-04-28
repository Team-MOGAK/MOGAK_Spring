package com.mogak.spring.service;

import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.result.user.UserCreateResult;
import com.mogak.spring.service.result.user.UserProfileResult;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.mogak.spring.web.dto.userdto.UserRequestDto.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final AddressRepository addressRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public UserCreateResult create(Long userId, CreateUserDto request, UploadImageDto uploadImageDto) {
        inputVerify(request);
        Job job = jobRepository.findJobByName(request.job())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        Address address = addressRepository.findAddressByName(request.address())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_ADDRESS));
        String profileImgUrl = uploadImageDto.imgUrl();
        String profileImgName = uploadImageDto.imgName();
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        if (user.getNickname() != null) {
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
        }
        user.registerUser(request.nickname(), job, address, profileImgUrl, profileImgName);
        JwtTokens tokens = issueUserTokens(user);
        return new UserCreateResult(user.getId(), user.getNickname(), tokens);
    }

    private Optional<User> findUserByNickname(String nickname) {
        return userRepository.findActiveByNickname(nickname);
    }

    protected void inputVerify(CreateUserDto request) {
        if (findUserByNickname(request.nickname()).isPresent())
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
    }

    public Boolean verifyNickname(String nickname) {
//        if (!Regex.USER_NICKNAME_REGEX.matchRegex(nickname, "NICKNAME"))
//            throw new UserException(ErrorCode.NOT_VALID_NICKNAME);
        if (findUserByNickname(nickname).isPresent())
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
        return true;
    }

    @Override
    public User getUserByEmail(String email) {
        return findUserByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
    }

    private Optional<User> findUserByEmail(String email) {
        verifyEmail(email);
        return userRepository.findActiveByEmail(email);
    }

    @Transactional
    @Override
    public void updateNickname(Long userId, UpdateNicknameDto nicknameDto) {
        verifyNickname(nicknameDto.nickname());
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateNickname(nicknameDto.nickname());
    }

    @Transactional
    @Override
    public void updateJob(Long userId, UpdateJobDto jobDto) {
        Job job = jobRepository.findJobByName(jobDto.job())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateJob(job);
    }


    protected void verifyEmail(String email) {
        if (!Regex.EMAIL_REGEX.matchRegex(email, "EMAIL"))
            throw new UserException(ErrorCode.NOT_VALID_EMAIL);
    }

//    @Override
//    public String getToken(User user) {
//        return jwtTokenHandler.createJwtToken(user.getId().toString());
//    }

    @Override
    public String getToken(User user) {
        return jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), resolveTokenRole(user));
    }

    private JwtTokens issueUserTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), resolveTokenRole(user));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);
        return new JwtTokens(accessToken, refreshToken);
    }

    private String resolveTokenRole(User user) {
        if (user.getNickname() != null && !user.getNickname().isEmpty() && user.getRole() != null) {
            return user.getRole().getKey();
        }
        return SecurityAuthority.PENDING.getAuthority();
    }


    @Override
    public String getProfileImgName(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String profileImgName = user.getProfileImgName();
        return profileImgName;
    }

    @Transactional
    @Override
    public void updateImg(Long userId, UserRequestDto.UpdateImageDto userImageDto) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String imgUrl = userImageDto.imgUrl();
        String imgName = userImageDto.imgName();
        user.updateProfileImg(imgUrl, imgName);
    }

    @Override
    public UserProfileResult getUserProfile(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String nickname = user.getNickname();
        String job = user.getJob().getName();
        String profileImgUrl = user.getProfileImgUrl();
        return new UserProfileResult(nickname, job, profileImgUrl);
    }
}

package com.mogak.spring.service;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.CommonException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.global.JwtArgumentResolver;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.login.JwtTokenProvider;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.util.Regex;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static com.mogak.spring.web.dto.UserRequestDto.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final AddressRepository addressRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public User create(CreateUserDto response) {
        inputVerify(response);
        Job job = jobRepository.findJobByName(response.getJob()).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        Address address = addressRepository.findAddressByName(response.getAddress()).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_ADDRESS));
        return userRepository.save(UserConverter.toUser(response, job, address));
    }
    @Override
    public Boolean findUserByNickname(String nickname) {
        if (userRepository.findOneByNickname(nickname).isPresent()) {
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
        }
        return false;
    }

    protected void inputVerify(CreateUserDto response) {
        if (!Regex.USER_NICKNAME_REGEX.matchRegex(response.getNickname(), "NICKNAME"))
            throw new UserException(ErrorCode.NOT_VALID_NICKNAME);
        if (!Regex.EMAIL_REGEX.matchRegex(response.getEmail(), "EMAIL"))
            throw new UserException(ErrorCode.NOT_VALID_EMAIL);
        findUserByNickname(response.getNickname());
    }

    public Boolean verifyNickname(String nickname) {
        if (!Regex.USER_NICKNAME_REGEX.matchRegex(nickname, "NICKNAME"))
            throw new UserException(ErrorCode.NOT_VALID_NICKNAME);
        return true;
    }

    @Override
    public User findUserByEmail(String email) {
        verifyEmail(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
    }

    @Transactional
    @Override
    public void updateNickname(UpdateNicknameDto nicknameDto, HttpServletRequest req) {
        verifyNickname(nicknameDto.getNickname());
        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateNickname(nicknameDto.getNickname());
    }

    @Transactional
    @Override
    public void updateJob(UpdateJobDto jobDto, HttpServletRequest req) {
        Job job = jobRepository.findJobByName(jobDto.getJob()).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateJob(job);
    }

    protected void verifyEmail(String email) {
        if (!Regex.EMAIL_REGEX.matchRegex(email, "EMAIL"))
            throw new UserException(ErrorCode.NOT_VALID_EMAIL);
    }

    @Override
    public HttpHeaders getHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenProvider.createJwtToken(user.getId().toString()));
        return headers;
    }

}

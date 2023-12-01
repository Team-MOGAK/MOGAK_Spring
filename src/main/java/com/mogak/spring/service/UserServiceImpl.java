package com.mogak.spring.service;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.login.JwtTokenHandler;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final JwtTokenHandler jwtTokenHandler;

    @Transactional
    @Override
    public User create(CreateUserDto response, UploadImageDto uploadImageDto) {
        inputVerify(response);
        Job job = jobRepository.findJobByName(response.getJob())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        Address address = addressRepository.findAddressByName(response.getAddress())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_ADDRESS));
        String profileImgUrl = uploadImageDto.getImgUrl();
        String profileImgName = uploadImageDto.getImgName();
        return userRepository.save(UserConverter.toUser(response, job, address, profileImgUrl, profileImgName));
    }

    private Optional<User> findUserByNickname(String nickname) {
        return userRepository.findOneByNickname(nickname);
    }

    protected void inputVerify(CreateUserDto response) {
        if (!Regex.EMAIL_REGEX.matchRegex(response.getEmail(), "EMAIL"))
            throw new UserException(ErrorCode.NOT_VALID_EMAIL);
        if (findUserByNickname(response.getNickname()).isPresent())
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
        if (findUserByEmail(response.getEmail()).isPresent()) {
            throw new UserException(ErrorCode.ALREADY_EXIST_USER);
        }
    }

    public Boolean verifyNickname(String nickname) {
        if (!Regex.USER_NICKNAME_REGEX.matchRegex(nickname, "NICKNAME"))
            throw new UserException(ErrorCode.NOT_VALID_NICKNAME);
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
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void updateNickname(Long userId, UpdateNicknameDto nicknameDto) {
        verifyNickname(nicknameDto.getNickname());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateNickname(nicknameDto.getNickname());
    }

    @Transactional
    @Override
    public void updateJob(Long userId, UpdateJobDto jobDto) {
        Job job = jobRepository.findJobByName(jobDto.getJob())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        user.updateJob(job);
    }


    protected void verifyEmail(String email) {
        if (!Regex.EMAIL_REGEX.matchRegex(email, "EMAIL"))
            throw new UserException(ErrorCode.NOT_VALID_EMAIL);
    }

    @Override
    public HttpHeaders getHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenHandler.createJwtToken(user.getId().toString()));
        return headers;
    }
    public String getProfileImgName(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String profileImgName = user.getProfileImgName();
        return profileImgName;
    }

    @Transactional
    @Override
    public void updateImg(Long userId, UserRequestDto.UpdateImageDto userImageDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String imgUrl = userImageDto.getImgUrl();
        String imgName = userImageDto.getImgName();
        user.updateProfileImg(imgUrl, imgName);
    }


}

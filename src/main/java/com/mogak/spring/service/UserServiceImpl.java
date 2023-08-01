package com.mogak.spring.service;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.UserRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public User create(UserRequestDto.CreateUserDto response) {
        inputVerify(response);
        Job job = jobRepository.findJobByName(response.getJob())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 직업입니다"));
        Address address = addressRepository.findAddressByName(response.getAddress())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다"));
        return userRepository.save(UserConverter.toUser(response, job, address));
    }
    @Override
    public Boolean findUserByNickname(String nickname) {
        return userRepository.findOneByNickname(nickname).isPresent();
    }

    private void inputVerify(UserRequestDto.CreateUserDto response) throws RuntimeException {
        if (!Regex.USER_NICKNAME_REGEX.matchRegex(response.getNickname()))
            throw new RuntimeException("올바른 닉네임이 아닙니다");
        if (!Regex.EMAIL_REGEX.matchRegex(response.getEmail()))
            throw new RuntimeException("올바른 이메일 형식이 아닙니다");
    }

}

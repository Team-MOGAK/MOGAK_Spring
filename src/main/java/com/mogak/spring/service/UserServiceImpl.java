package com.mogak.spring.service;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
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
    @Override
    public User create(UserRequestDto.CreateUserDto response) {
        User user = userRepository.save(UserConverter.toUser(response));
        return null;
    }
    @Override
    public Boolean findUserByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname).isPresent();
    }

}

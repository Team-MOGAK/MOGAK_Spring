package com.mogak.spring.service;

import com.mogak.spring.converter.FollowConverter;
import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorCode;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.repository.FollowRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.FollowRequestDto;
import com.mogak.spring.web.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mogak.spring.web.dto.UserResponseDto.*;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void follow(String nickname, HttpServletRequest req) {
        Long userId = Long.valueOf(req.getParameter("userId"));
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        User toUser = userRepository.findOneByNickname(nickname).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));

        if (followRepository.findByFromAndTo(fromUser, toUser).isPresent()) {
            throw new UserException(ErrorCode.ALREADY_CREATE_FOLLOW);
        }
        followRepository.save(FollowConverter.toFollow(fromUser, toUser));
    }

    @Transactional
    @Override
    public void unfollow(String nickname, HttpServletRequest req) {
        Long userId = Long.valueOf(req.getParameter("userId"));
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        User toUser = userRepository.findOneByNickname(nickname).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));

        Follow follow = followRepository.findByFromAndTo(fromUser, toUser).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_FOLLOW));
        followRepository.delete(follow);
    }

    @Override
    public FollowRequestDto.CountDto getFollowCount(String nickname) {
        User user = userRepository.findOneByNickname(nickname).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return FollowRequestDto.CountDto.builder()
                .motoCnt(getMotoCount(user))
                .mentorCnt(getMentorCount(user))
                .build();
    }

    @Override
    public List<UserDto> getMotoList(String nickname) {
        User user = userRepository.findOneByNickname(nickname).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<User> users = followRepository.findMotosByUser(user);
        return users.stream()
                .map(u -> UserDto.builder()
                        .nickname(u.getNickname())
                        .job(u.getJob().getName())
                        .address(u.getAddress().getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getMentorList(String nickname) {
        User user = userRepository.findOneByNickname(nickname).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<User> users = followRepository.findMentorsByUser(user);
        return users.stream()
                .map(u -> UserDto.builder()
                        .nickname(u.getNickname())
                        .job(u.getJob().getName())
                        .address(u.getAddress().getName())
                        .build())
                .collect(Collectors.toList());
    }

    private int getMotoCount(User user) {
        return followRepository.findMotoCntByUser(user);
    }

    private int getMentorCount(User user) {
        return followRepository.findMentorCntByUser(user);
    }
}
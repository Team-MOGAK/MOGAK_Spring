package com.mogak.spring.service;

import com.mogak.spring.converter.FollowConverter;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorCode;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.repository.FollowRepository;
import com.mogak.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

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


        followRepository.save(FollowConverter.toFollow(fromUser, toUser));


    }
}

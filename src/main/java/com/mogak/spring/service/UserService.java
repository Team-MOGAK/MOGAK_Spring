package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UserCreateResult;
import com.mogak.spring.service.result.UserProfileResult;

import java.util.List;

public interface UserService {
    UserCreateResult create(
            Long userId,
            String nickname,
            String job,
            String address,
            ProfileImageResult profileImage,
            List<UserConsentCommand> consents
    );

    Boolean verifyNickname(String request);
  
    String getToken(User user);
  
    void updateNickname(Long userId, String nickname);
    String getProfileImgName(Long userId);
  
    void updateJob(Long userId, String job);
  
    User getUserByEmail(String email);
  
    void updateImg(Long userId, ProfileImageResult profileImage);

    UserProfileResult getUserProfile(Long userId);

}

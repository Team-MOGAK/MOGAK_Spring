package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;

public interface UserService {

    UserResponseDto.CreateDto create(Long userId, UserRequestDto.CreateUserDto request, UserRequestDto.UploadImageDto uploadImageDto);
    Boolean verifyNickname(String request);
  
    String getToken(User user);
  
    void updateNickname(Long userId, UserRequestDto.UpdateNicknameDto nicknameDto);
    String getProfileImgName(Long userId);
  
    void updateJob(Long userId, UserRequestDto.UpdateJobDto jobDto);
  
    User getUserByEmail(String email);
  
    void updateImg(Long userId, UserRequestDto.UpdateImageDto userImageDto);

    UserResponseDto.GetUserDto getUserProfile(Long userId);

}

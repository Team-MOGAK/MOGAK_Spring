package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import org.springframework.http.HttpHeaders;

public interface UserService {

    UserResponseDto.CreateDto create(UserRequestDto.CreateUserDto request, UserRequestDto.UploadImageDto uploadImageDto);
    Boolean verifyNickname(String request);
  
    String getToken(User user);
  
    void updateNickname(UserRequestDto.UpdateNicknameDto nicknameDto);
    String getProfileImgName();
  
    void updateJob(UserRequestDto.UpdateJobDto jobDto);
  
    User getUserByEmail(String email);
  
    void updateImg(UserRequestDto.UpdateImageDto userImageDto);

    UserResponseDto.GetUserDto getUserProfile();

}

package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface UserService {

    User create(UserRequestDto.CreateUserDto request, UserRequestDto.UploadImageDto uploadImageDto);
    Boolean verifyNickname(String request);
  
    HttpHeaders getHeader(User user);
  
    void updateNickname(Long userId, UserRequestDto.UpdateNicknameDto nicknameDto);
    String getProfileImgName(Long userId);
  
    void updateJob(Long userId, UserRequestDto.UpdateJobDto jobDto);
  
    User getUserByEmail(String email);
  
    void updateImg(Long userId, UserRequestDto.UpdateImageDto userImageDto);

    String getAuthorizationCode();
}

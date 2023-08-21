package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface UserService {

    User create(UserRequestDto.CreateUserDto request, UserRequestDto.UploadImageDto uploadImageDto);
    Boolean findUserByNickname(String nickname);
    Boolean verifyNickname(String request);
  
    HttpHeaders getHeader(User user);
  
    void updateNickname(UserRequestDto.UpdateNicknameDto nicknameDto, HttpServletRequest req);
    String getProfileImgName(HttpServletRequest req);
  
    void updateJob(UserRequestDto.UpdateJobDto jobDto, HttpServletRequest req);
  
    User getUserByEmail(String email);
  
    void updateImg(UserRequestDto.UpdateImageDto userImageDto, HttpServletRequest req);

}

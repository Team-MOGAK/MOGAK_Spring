package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    List<PostImgRequestDto.CreatePostImgDto> uploadImg(List<MultipartFile> multipartFile, String dirName);

    UserRequestDto.UploadImageDto uploadProfileImg(MultipartFile request, String dirName);

    void deleteImg(List<PostImg> postImgList, String dirName);

    void deleteProfileImg(String profileImgName);

    UserRequestDto.UpdateImageDto updateProfileImg(MultipartFile request, String profileImgName, String dirName);
}

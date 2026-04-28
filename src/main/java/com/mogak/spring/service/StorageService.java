package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UploadedPostImageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    List<UploadedPostImageResult> uploadImg(List<MultipartFile> multipartFile, String dirName);

    ProfileImageResult uploadProfileImg(MultipartFile request, String dirName);

    void deleteImg(List<PostImg> postImgList, String dirName);

    void deleteProfileImg(String profileImgName);

    ProfileImageResult updateProfileImg(MultipartFile request, String profileImgName, String dirName);
}

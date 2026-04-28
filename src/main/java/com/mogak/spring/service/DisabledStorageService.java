package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UploadedPostImageResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "feature.storage", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledStorageService implements StorageService {

    @Override
    public List<UploadedPostImageResult> uploadImg(List<MultipartFile> multipartFile, String dirName) {
        throw storageDisabled();
    }

    @Override
    public ProfileImageResult uploadProfileImg(MultipartFile request, String dirName) {
        throw storageDisabled();
    }

    @Override
    public void deleteImg(List<PostImg> postImgList, String dirName) {
        throw storageDisabled();
    }

    @Override
    public void deleteProfileImg(String profileImgName) {
        throw storageDisabled();
    }

    @Override
    public ProfileImageResult updateProfileImg(MultipartFile request, String profileImgName, String dirName) {
        throw storageDisabled();
    }

    private BaseException storageDisabled() {
        return new BaseException(ErrorCode.STORAGE_DISABLED);
    }
}

package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageCleanupService {

    private final StorageService storageService;

    public void deletePostImagesAfterCommit(List<PostImg> postImages, String dirName) {
        if (postImages == null || postImages.isEmpty()) {
            return;
        }
        List<PostImg> cleanupTargets = List.copyOf(postImages);
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deletePostImagesBestEffort(cleanupTargets, dirName);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deletePostImagesBestEffort(cleanupTargets, dirName);
            }
        });
    }

    public void deleteUploadedImagesBestEffort(List<PostImgRequestDto.CreatePostImgDto> uploadedImages, String dirName) {
        if (uploadedImages == null || uploadedImages.isEmpty()) {
            return;
        }
        List<PostImg> cleanupTargets = uploadedImages.stream()
                .map(uploadedImage -> PostImg.builder()
                        .imgName(uploadedImage.imgName())
                        .imgUrl(uploadedImage.imgUrl())
                        .build())
                .toList();
        deletePostImagesBestEffort(cleanupTargets, dirName);
    }

    private void deletePostImagesBestEffort(List<PostImg> postImages, String dirName) {
        try {
            storageService.deleteImg(postImages, dirName);
        } catch (RuntimeException e) {
            log.warn("Failed to cleanup post images. dirName={}, imageCount={}", dirName, postImages.size(), e);
        }
    }
}

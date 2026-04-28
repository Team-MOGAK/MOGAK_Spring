package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class StorageCleanupServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private StorageCleanupService storageCleanupService;

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("트랜잭션이 활성화되어 있으면 이미지 삭제를 afterCommit까지 미룬다")
    void deletePostImagesAfterCommitDefersCleanupUntilCommit() {
        PostImg postImg = PostImg.builder()
                .imgName("post.png")
                .imgUrl("https://example.com/post.png")
                .build();
        TransactionSynchronizationManager.initSynchronization();

        storageCleanupService.deletePostImagesAfterCommit(List.of(postImg), "img");

        verifyNoInteractions(storageService);

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

        verify(storageService).deleteImg(List.of(postImg), "img");
    }

    @Test
    @DisplayName("업로드 보상 삭제는 업로드 DTO를 삭제 대상 이미지로 변환한다")
    void deleteUploadedImagesBestEffortConvertsUploadedDtos() {
        List<PostImgRequestDto.CreatePostImgDto> uploadedImages = List.of(
                new PostImgRequestDto.CreatePostImgDto(
                        "uploaded.png",
                        "https://example.com/uploaded.png",
                        true
                )
        );

        storageCleanupService.deleteUploadedImagesBestEffort(uploadedImages, "img");

        verify(storageService).deleteImg(
                argThat(images -> images.size() == 1 && "uploaded.png".equals(images.get(0).getImgName())),
                eq("img")
        );
    }

    @Test
    @DisplayName("이미지 cleanup 실패는 원래 흐름을 덮지 않는다")
    void cleanupFailureDoesNotPropagate() {
        PostImg postImg = PostImg.builder()
                .imgName("post.png")
                .imgUrl("https://example.com/post.png")
                .build();
        doThrow(new RuntimeException("storage down"))
                .when(storageService)
                .deleteImg(List.of(postImg), "img");

        assertThatCode(() -> storageCleanupService.deletePostImagesAfterCommit(List.of(postImg), "img"))
                .doesNotThrowAnyException();
    }
}

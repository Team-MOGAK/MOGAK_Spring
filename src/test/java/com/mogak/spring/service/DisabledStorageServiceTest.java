package com.mogak.spring.service;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.catchThrowable;

class DisabledStorageServiceTest {

    private final DisabledStorageService disabledStorageService = new DisabledStorageService();

    @Test
    @DisplayName("게시글 이미지 업로드는 storage 비활성화 예외를 반환한다")
    void uploadImgThrowsStorageDisabled() {
        Throwable throwable = catchThrowable(() -> disabledStorageService.uploadImg(null, "img"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.STORAGE_DISABLED);
    }

    @Test
    @DisplayName("프로필 이미지 업로드는 storage 비활성화 예외를 반환한다")
    void uploadProfileImgThrowsStorageDisabled() {
        Throwable throwable = catchThrowable(() -> disabledStorageService.uploadProfileImg(null, "profile"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.STORAGE_DISABLED);
    }

    @Test
    @DisplayName("게시글 이미지 삭제는 storage 비활성화 예외를 반환한다")
    void deleteImgThrowsStorageDisabled() {
        Throwable throwable = catchThrowable(() -> disabledStorageService.deleteImg(null, "img"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.STORAGE_DISABLED);
    }

    @Test
    @DisplayName("프로필 이미지 삭제는 storage 비활성화 예외를 반환한다")
    void deleteProfileImgThrowsStorageDisabled() {
        Throwable throwable = catchThrowable(() -> disabledStorageService.deleteProfileImg("profile.png"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.STORAGE_DISABLED);
    }

    @Test
    @DisplayName("프로필 이미지 갱신은 storage 비활성화 예외를 반환한다")
    void updateProfileImgThrowsStorageDisabled() {
        Throwable throwable = catchThrowable(() -> disabledStorageService.updateProfileImg(null, "profile.png", "profile"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.STORAGE_DISABLED);
    }
}

package com.mogak.spring.service;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

    @Mock private S3Client s3Client;

    private AwsS3Service awsS3Service;

    @BeforeEach
    void setUp() {
        awsS3Service = new AwsS3Service(s3Client);
        ReflectionTestUtils.setField(awsS3Service, "bucket", "test-bucket");
    }

    @Test
    @DisplayName("이미지 목록이 비어 있으면 400 에러를 반환한다")
    void uploadImgThrowsWhenListEmpty() {
        Throwable throwable = catchThrowable(() -> awsS3Service.uploadImg(List.of(), "img"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_HAVE_IMAGE);
    }

    @Test
    @DisplayName("원본 파일명이 없으면 프로필 이미지 업로드를 거부한다")
    void uploadProfileImgThrowsWhenOriginalFilenameMissing() {
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile",
                null,
                "image/png",
                "png".getBytes()
        );

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadProfileImg(file, "profile"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
    }

    @Test
    @DisplayName("확장자가 없으면 프로필 이미지 업로드를 거부한다")
    void uploadProfileImgThrowsWhenExtensionMissing() {
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile",
                "profile",
                "image/png",
                "png".getBytes()
        );

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadProfileImg(file, "profile"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
    }

    @Test
    @DisplayName("이미지 content type이 아니면 프로필 이미지 업로드를 거부한다")
    void uploadProfileImgThrowsWhenContentTypeInvalid() {
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                "text/plain",
                "png".getBytes()
        );

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadProfileImg(file, "profile"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
    }
}

package com.mogak.spring.service;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.service.result.UploadedPostImageResult;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

    @Mock private S3Client s3Client;
    @Mock private S3Utilities s3Utilities;

    private AwsS3Service awsS3Service;
    private static byte[] pngBytes;

    @BeforeAll
    static void createPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        pngBytes = outputStream.toByteArray();
    }

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
    @DisplayName("게시글 이미지 업로드는 첫 이미지 원본과 썸네일을 S3에 업로드한다")
    void uploadImgUploadsOriginalAndThumbnailForFirstImage() throws Exception {
        MockMultipartFile file = pngFile("post.png");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(s3Client.utilities()).thenReturn(s3Utilities);
        doReturn(URI.create("https://example.com/post.png").toURL())
                .when(s3Utilities)
                .getUrl(any(GetUrlRequest.class));
        ArgumentCaptor<PutObjectRequest> putCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        List<UploadedPostImageResult> results = awsS3Service.uploadImg(List.of(file), "img");

        verify(s3Client, times(2)).putObject(putCaptor.capture(), any(RequestBody.class));
        assertThat(putCaptor.getAllValues())
                .extracting(PutObjectRequest::contentType)
                .containsExactly("image/png", "image/png");
        assertThat(putCaptor.getAllValues().get(0).contentLength()).isEqualTo(file.getSize());
        assertThat(putCaptor.getAllValues().get(1).contentLength()).isPositive();
        assertThat(results)
                .extracting(UploadedPostImageResult::isThumbnail)
                .containsExactly(false, true);
    }

    @Test
    @DisplayName("게시글 이미지 업로드 중 썸네일 업로드가 실패하면 이미 올라간 원본 이미지를 삭제한다")
    void uploadImgCleansUpOriginalWhenThumbnailUploadFails() throws Exception {
        MockMultipartFile file = pngFile("post.png");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build())
                .thenThrow(new RuntimeException("thumbnail upload failed"));
        when(s3Client.utilities()).thenReturn(s3Utilities);
        doReturn(URI.create("https://example.com/post.png").toURL())
                .when(s3Utilities)
                .getUrl(any(GetUrlRequest.class));
        ArgumentCaptor<PutObjectRequest> putCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadImg(List.of(file), "img"));

        assertThat(throwable).isInstanceOf(RuntimeException.class);
        verify(s3Client, times(2)).putObject(putCaptor.capture(), any(RequestBody.class));
        String originalKey = putCaptor.getAllValues().get(0).key();
        String thumbnailKey = putCaptor.getAllValues().get(1).key();
        ArgumentCaptor<DeleteObjectRequest> deleteCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(2)).deleteObject(deleteCaptor.capture());
        assertThat(deleteCaptor.getAllValues().stream().map(DeleteObjectRequest::key))
                .containsExactly(originalKey, thumbnailKey);
    }

    @Test
    @DisplayName("게시글 이미지 URL 생성이 실패해도 이미 올라간 원본 이미지를 삭제한다")
    void uploadImgCleansUpOriginalWhenObjectUrlCreationFails() {
        MockMultipartFile file = pngFile("post.png");
        RuntimeException urlException = new RuntimeException("url failed");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Utilities.getUrl(any(GetUrlRequest.class))).thenThrow(urlException);
        ArgumentCaptor<PutObjectRequest> putCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadImg(List.of(file), "img"));

        assertThat(throwable).isSameAs(urlException);
        verify(s3Client).putObject(putCaptor.capture(), any(RequestBody.class));
        ArgumentCaptor<DeleteObjectRequest> deleteCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deleteCaptor.capture());
        assertThat(deleteCaptor.getValue().key()).isEqualTo(putCaptor.getValue().key());
    }

    @Test
    @DisplayName("여러 게시글 이미지 업로드 중 뒤쪽 파일이 실패하면 앞서 올라간 원본과 썸네일을 삭제한다")
    void uploadImgCleansUpPreviousObjectsWhenLaterFileUploadFails() throws Exception {
        MockMultipartFile firstFile = pngFile("first.png");
        MockMultipartFile secondFile = pngFile("second.png");
        RuntimeException uploadException = new RuntimeException("second upload failed");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build())
                .thenReturn(PutObjectResponse.builder().build())
                .thenThrow(uploadException);
        when(s3Client.utilities()).thenReturn(s3Utilities);
        doReturn(URI.create("https://example.com/post.png").toURL())
                .when(s3Utilities)
                .getUrl(any(GetUrlRequest.class));
        ArgumentCaptor<PutObjectRequest> putCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadImg(List.of(firstFile, secondFile), "img"));

        assertThat(throwable).isSameAs(uploadException);
        verify(s3Client, times(3)).putObject(putCaptor.capture(), any(RequestBody.class));
        List<String> attemptedKeys = putCaptor.getAllValues().stream()
                .map(PutObjectRequest::key)
                .toList();
        ArgumentCaptor<DeleteObjectRequest> deleteCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(3)).deleteObject(deleteCaptor.capture());
        assertThat(deleteCaptor.getAllValues().stream().map(DeleteObjectRequest::key))
                .containsExactlyElementsOf(attemptedKeys);
    }

    @Test
    @DisplayName("게시글 이미지 업로드 보상 삭제가 실패해도 원래 업로드 예외를 유지한다")
    void uploadImgKeepsOriginalExceptionWhenCleanupFails() throws Exception {
        MockMultipartFile file = pngFile("post.png");
        RuntimeException uploadException = new RuntimeException("thumbnail upload failed");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build())
                .thenThrow(uploadException);
        when(s3Client.utilities()).thenReturn(s3Utilities);
        doReturn(URI.create("https://example.com/post.png").toURL())
                .when(s3Utilities)
                .getUrl(any(GetUrlRequest.class));
        doThrow(S3Exception.builder().message("delete failed").build())
                .when(s3Client)
                .deleteObject(any(DeleteObjectRequest.class));

        Throwable throwable = catchThrowable(() -> awsS3Service.uploadImg(List.of(file), "img"));

        assertThat(throwable).isSameAs(uploadException);
        verify(s3Client, times(2)).deleteObject(any(DeleteObjectRequest.class));
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

    private MockMultipartFile pngFile(String fileName) {
        return new MockMultipartFile(
                "multipartFile",
                fileName,
                "image/png",
                pngBytes
        );
    }
}

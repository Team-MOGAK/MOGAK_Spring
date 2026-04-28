package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UploadedPostImageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "feature.storage", name = "enabled", havingValue = "true")
public class AwsS3Service implements StorageService {

    private static final Set<String> SUPPORTED_IMAGE_FORMATS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    @Override
    public List<UploadedPostImageResult> uploadImg(List<MultipartFile> multipartFile, String dirName) {
        List<UploadedPostImageResult> uploadedImages = new ArrayList<>();
        if (multipartFile.isEmpty()) {
            throw new BaseException(ErrorCode.NOT_HAVE_IMAGE);
        }
        List<String> uploadedObjectNames = new ArrayList<>();
        try {
            multipartFile.forEach(img -> {
                String format = extractImageFormat(img);
                String imgName = createImgName(format, dirName);
                uploadedObjectNames.add(imgName);
                uploadImgToS3(imgName, img, format);
                uploadedImages.add(new UploadedPostImageResult(
                        imgName,
                        createObjectUrl(imgName),
                        false
                ));
                if (multipartFile.get(0) == img) {
                    String thumbnailImgName = createThumbnailImgName(format, dirName);
                    MultipartFile thumbnailImg = resizeImage(thumbnailImgName, format, img, 200, 200);
                    uploadedObjectNames.add(thumbnailImgName);
                    uploadThumbnailToS3(thumbnailImgName, thumbnailImg, format);
                    uploadedImages.add(new UploadedPostImageResult(
                            thumbnailImgName,
                            createObjectUrl(thumbnailImgName),
                            true
                    ));
                }
            });
        } catch (RuntimeException e) {
            deleteUploadedObjectsBestEffort(uploadedObjectNames);
            throw e;
        }
        return uploadedImages;
    }

    private void uploadThumbnailToS3(String thumbnailImgName, MultipartFile thumbnailImg, String format) {
        try (InputStream inputThumbnailStream = thumbnailImg.getInputStream()) {
            putObject(thumbnailImgName, thumbnailImg.getSize(), contentTypeForFormat(format), inputThumbnailStream);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 썸네일 업로드 실패했습니다");
        }
    }

    private void uploadImgToS3(String imgName, MultipartFile multipartFile, String format) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            putObject(imgName, multipartFile.getSize(), resolveContentType(multipartFile, format), inputStream);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 업로드 실패했습니다");
        }
    }

    private PutObjectResponse putObject(String key, long size, String contentType, InputStream inputStream) {
        return s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentLength(size)
                        .contentType(contentType)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromInputStream(inputStream, size)
        );
    }

    private MultipartFile resizeImage(String thumbnailImgName, String imgFormat, MultipartFile multipartFile, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            if (image == null) {
                throw invalidImageRequest();
            }
            MarvinImage marvinImage = new MarvinImage(image);
            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", width);
            scale.setAttribute("newHeight", height);
            scale.process(marvinImage.clone(), marvinImage, null, null, false);

            BufferedImage imageNoAlpha = marvinImage.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, imgFormat, baos);
            baos.flush();
            return new MockMultipartFile(thumbnailImgName, baos.toByteArray());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fail to generate thumbnail");
        }
    }

    @Override
    public ProfileImageResult uploadProfileImg(MultipartFile request, String dirName) {
        validateImagePresent(request);
        String format = extractImageFormat(request);
        String imgName = createImgName(format, dirName);
        try (InputStream inputStream = request.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(imgName)
                            .contentLength(request.getSize())
                            .contentType(resolveContentType(request, format))
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromInputStream(inputStream, request.getSize())
            );
            log.info("s3 업로드 성공!");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 업로드 실패했습니다");
        }
        return new ProfileImageResult(imgName, createObjectUrl(imgName));
    }

    @Override
    public void deleteImg(List<PostImg> postImgList, String dirName) {
        if (postImgList.isEmpty()) {
            throw new BaseException(ErrorCode.NOT_HAVE_IMAGE);
        }
        for (PostImg postImg : postImgList) {
            deleteObject(postImg.getImgName());
        }
    }

    @Override
    public void deleteProfileImg(String profileImgName) {
        if (profileImgName != null) {
            deleteObject(profileImgName);
        } else {
            throw new BaseException(ErrorCode.NOT_HAVE_IMAGE);
        }
    }

    private void deleteUploadedObjectsBestEffort(List<String> uploadedObjectNames) {
        for (String objectName : uploadedObjectNames) {
            try {
                deleteObject(objectName);
            } catch (RuntimeException cleanupException) {
                log.warn("Failed to cleanup uploaded S3 object. objectName={}", objectName, cleanupException);
            }
        }
    }

    private void deleteObject(String objectName) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(objectName).build());
    }

    @Override
    public ProfileImageResult updateProfileImg(MultipartFile request, String profileImgName, String dirName) {
        validateImagePresent(request);
        if (profileImgName != null) {
            deleteProfileImg(profileImgName);
        }
        String format = extractImageFormat(request);
        String imgName = createImgName(format, dirName);
        try (InputStream inputStream = request.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(imgName)
                            .contentLength(request.getSize())
                            .contentType(resolveContentType(request, format))
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromInputStream(inputStream, request.getSize())
            );
            log.info("s3 업로드 성공!");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 업로드 실패했습니다");
        }
        return new ProfileImageResult(imgName, createObjectUrl(imgName));
    }

    private void validateImagePresent(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BaseException(ErrorCode.NOT_HAVE_IMAGE);
        }
    }

    private String extractImageFormat(MultipartFile multipartFile) {
        validateImagePresent(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw invalidImageRequest();
        }

        int extensionIndex = originalFilename.lastIndexOf(".");
        if (extensionIndex < 0 || extensionIndex == originalFilename.length() - 1) {
            throw invalidImageRequest();
        }

        String format = originalFilename.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
        if (!SUPPORTED_IMAGE_FORMATS.contains(format)) {
            throw invalidImageRequest();
        }

        String contentType = multipartFile.getContentType();
        if (StringUtils.hasText(contentType) && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw invalidImageRequest();
        }

        return format;
    }

    private String resolveContentType(MultipartFile multipartFile, String format) {
        String contentType = multipartFile.getContentType();
        if (StringUtils.hasText(contentType)) {
            return contentType;
        }
        return contentTypeForFormat(format);
    }

    private String contentTypeForFormat(String format) {
        return switch (format) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "webp" -> "image/webp";
            default -> throw invalidImageRequest();
        };
    }

    private String createImgName(String format, String dirName) {
        return dirName + "/" + UUID.randomUUID() + "." + format;
    }

    private String createThumbnailImgName(String format, String dirName) {
        return dirName + "/" + "s_" + UUID.randomUUID() + "." + format;
    }

    private BaseException invalidImageRequest() {
        return new BaseException(ErrorCode.INVALID_PARAMETER_ERROR);
    }

    private String createObjectUrl(String key) {
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build())
                .toExternalForm();
    }
}

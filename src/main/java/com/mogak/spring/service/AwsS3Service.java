package com.mogak.spring.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.repository.PostImgRepository;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostImgResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final PostImgRepository postImgRepository;

    // 이미지 파일&썸네일 s3 업로드
    public List<PostImgRequestDto.CreatePostImgDto> uploadImg(List<MultipartFile> multipartFile, String dirName) {

        List<PostImgRequestDto.CreatePostImgDto> postImgRequestDtoList = new ArrayList<>();
        //multifle을 따로 file로 만드는 것이 아니라 inputstream을 받는 방식
        multipartFile.forEach(img -> {
            String imgName = createImgName(img.getOriginalFilename(), dirName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(img.getSize());
            objectMetadata.setContentType(img.getContentType());
            //s3 업로드
            try(InputStream inputStream = img.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, imgName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                log.info("s3 업로드 성공!");
            } catch(IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 업로드 실패했습니다");
            }
            postImgRequestDtoList.add(PostImgRequestDto.CreatePostImgDto.builder()
                    .imgName(imgName)
                    .imgUrl(amazonS3.getUrl(bucket, imgName).toString())
                    .thumbnail(false)
                    .build());
            //첫번째 이미지 썸네일 업로드
            if(multipartFile.get(0) == img) {
                String thumbnailImgName = createThumbnailImgName(img.getOriginalFilename(), dirName);
                String format = createImgFormat(img);
                MultipartFile thumbnailImg = resizeImage(thumbnailImgName, format, img, 200, 200);
                ObjectMetadata objectThumbnailMetadata = new ObjectMetadata();
                objectThumbnailMetadata.setContentLength(thumbnailImg.getSize());
                objectThumbnailMetadata.setContentType("image/"+format);
                try(InputStream inputThumbnailStream = thumbnailImg.getInputStream()) {
                    amazonS3.putObject(new PutObjectRequest(bucket, thumbnailImgName, inputThumbnailStream, objectThumbnailMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    log.info("s3 썸네일 업로드 성공!");
                } catch(IOException e){
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "s3 썸네일 업로드 실패했습니다");
                }
                //imgdto 저장
                postImgRequestDtoList.add(PostImgRequestDto.CreatePostImgDto.builder()
                        .imgName(thumbnailImgName)
                        .imgUrl(amazonS3.getUrl(bucket, thumbnailImgName).toString())
                        .thumbnail(true)
                        .build());
            }
        });
        return postImgRequestDtoList;
    }
    //썸네일 이미지 생성, 추후 프론트로부터 이미지 사이즈 값 받아오기
    private MultipartFile resizeImage(String thumbnailImgName, String imgFormat, MultipartFile multipartFile, int width, int height){
        try{
            //convert multipartfile to bufferedimage
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            MarvinImage marvinImage = new MarvinImage(image);
            Scale scale = new Scale();
            scale.load();;
            scale.setAttribute("newWidth", width);
            scale.setAttribute("newHeight", height);
            scale.process(marvinImage.clone(), marvinImage, null, null, false);

            BufferedImage imageNoAlpha = marvinImage.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, imgFormat, baos);
            baos.flush();
            return new MockMultipartFile(thumbnailImgName,baos.toByteArray());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Fail to generate thumbnail");
        }
    }
    //이미지포맷 추출
    private String createImgFormat(MultipartFile multipartFile){
        String format = multipartFile.getContentType().substring(multipartFile.getContentType().lastIndexOf("/") + 1);
        return format;
    }

    //s3 이미지객체 delete
    public void deleteImg(List<PostImg> postImgList, String dirName){
        for(PostImg postImg : postImgList){
            String imgName = postImg.getImgName();
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, imgName));
        }
    }
    //파일 업로드할 시 파일명 난수화를 위해 uuid 생성
    private String createImgName(String imgName, String dirName){
        String end = imgName.substring(imgName.indexOf(".")+1); // 이미지 형식 . 뒷부분 추출
        return dirName + "/" + UUID.randomUUID().toString() + "." + end;
    }

    private String createThumbnailImgName(String imgName,String dirName){
        String end = imgName.substring(imgName.indexOf(".")+1);
        return dirName + "/" + "s_" + UUID.randomUUID().toString() + "." + end;
    }

}

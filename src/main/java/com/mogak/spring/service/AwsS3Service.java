package com.mogak.spring.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mogak.spring.repository.PostImgRepository;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostImgResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
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

    // 이미지 파일 s3 업로드
    public List<PostImgRequestDto.CreatePostImgDto> uploadImg(List<MultipartFile> multipartFile, String dirName){

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
                    .build());

        });
        return postImgRequestDtoList;
    }
    //s3 이미지 delete
    public void deleteImg(String imgName, String dirName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, dirName + "/" + imgName));
    }
    //파일 업로드할 시 파일명 난수화를 위해 uuid 생성
    private String createImgName(String imgName, String dirName){
        String end = imgName.substring(imgName.indexOf(".")+1); // 이미지 형식 . 뒷부분 추출
        return dirName + "/" + UUID.randomUUID().toString() + "." + end;
    }



}

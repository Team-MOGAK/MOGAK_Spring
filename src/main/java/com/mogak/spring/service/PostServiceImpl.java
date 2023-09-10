package com.mogak.spring.service;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.converter.PostImgConverter;
import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto.NetworkPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MogakRepository mogakRepository;
    private final UserRepository userRepository;
    private final PostImgRepository postImgRepository;
    private final PostCommentRepository postCommentRepository;

    //회고록 & 회고록 이미지 생성 => 리팩토링 필요
    @Transactional
    @Override
    public Post create(Long userId, PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList, /*User user,*/Long mogakId) {
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
//        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if (request.getContents().length() > 350) {
            throw new PostException(ErrorCode.EXCEED_MAX_NUM_POST);
        }
        Post post = PostConverter.toPost(request, user, mogak);
        if (postImgDtoList.isEmpty()) {
            throw new PostException(ErrorCode.NOT_HAVE_IMAGE);
        }
        for (PostImgRequestDto.CreatePostImgDto postImgDto : postImgDtoList) {
            PostImg postImg = PostImgConverter.toPostImg(postImgDto, post);
            //썸네일이미지인지 체크 필요
            if (postImgDto.isThumbnail()) {
                post.putPostThumbnailUrl(postImg.getImgUrl()); //썸네일 이미지는 thumbnailurl에 추가
            } else {//이미지 업로드 체크
                post.putPostImg(postImg);
            }
            postImgRepository.save(postImg);
        }
        return postRepository.save(post);
    }

    //회고록 조회 - 무한 스크롤
    @Override
    public Slice<Post> getAllPosts(int page, Long mogakId, int size) {
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(() ->
                new MogakException(ErrorCode.NOT_EXIST_MOGAK)
        );
        Pageable pageable=PageRequest.of(page, size);

        Slice<Post> posts = postRepository.findAllPosts( mogakId, pageable);
        return posts;
    }

    //회고록 상세 조회 + 댓글, 이미지 같이 보이게
    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
    }


    //회고록 수정
    @Transactional
    @Override
    public Post update(Long postId, PostRequestDto.UpdatePostDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        if (request.getContents().length() > 350) {
            throw new PostException(ErrorCode.EXCEED_MAX_NUM_POST);
        }
        post.updatePost(request.getContents());
        return post;
    }

    //회고록 삭제
    @Transactional
    @Override
    public void delete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        //이미지 삭제
        postImgRepository.deleteAllByPost(post);
        //댓글 삭제
        postCommentRepository.deleteAllByPost(post);
        //회고록 삭제
        postRepository.deleteById(postId);
    }

    @Override
    public List<NetworkPostDto> getPacemakerPosts(Long userId, int cursor, int size) {
//        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Pageable pageable = PageRequest.of(cursor, size);
        List<Post> posts = postRepository.findPacemakerPostsByUser(user, pageable);
        //postImg 중 썸네일 이미지는 제외
        return posts.stream()
                .map(p -> {
                    List<String> imgUrls = p.getPostImgs().stream()
                            .filter(img -> img.getImgUrl() != p.getPostThumbnailUrl())
                            .map(PostImg::getImgUrl)
                            .collect(Collectors.toList());
                    NetworkPostDto dto = NetworkPostDto.builder()
                            .user(UserConverter.toUserDto(p.getUser()))
                            .contents(p.getContents())
                            .imgUrls(imgUrls)
                            .comments(p.getPostComments().stream()
                                    .map(CommentConverter::toNetworkCommentDto)
                                    .collect(Collectors.toList()))
                            .likeCnt(p.getLikeCnt())
                            .viewCnt(p.getViewCnt())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //전체 네트워킹 조회 - 이미지 썸네일 제외 반환
    @Override
    public Slice<Post> getNetworkPosts(Long userId, int page, int size, String sort, String address /*List<String> categoryList,*/){
//        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if(address == null){
            address = user.getAddress().getName();
        }
        String job = user.getJob().getName();
        Pageable pageable = PageRequest.of(page, size);
        Slice<Post> posts = postRepository.findNetworkPosts(address, job, sort, pageable);
        return posts;
    }

    //postId로 해당 회고록에 대한 이미지 url 반환
    @Override
    public List<String> findImgUrlByPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        List<PostImg> postImgList = postImgRepository.findAllByPost(post);
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg :postImgList) {
            imgUrlList.add(postImg.getImgUrl());
        }
        return imgUrlList;
    }

    //이미지 상세조회를 위한, 썸네일 제외 url 반환
    @Override
    public List<String> findNotThumbnailImg(Post post) {
        String thumbnailUrl = post.getPostThumbnailUrl();
        List<PostImg> postImgList = post.getPostImgs();
        List<String> imgUrls = new ArrayList<>();
        for (PostImg postImg : postImgList) {
            if (!thumbnailUrl.equals(postImg.getImgUrl())) {
                imgUrls.add(postImg.getImgUrl());
            }
        }
        return imgUrls;
    }
    //회고록에 대한 모든 img 반환
    @Override
    public List<PostImg> findAllImgByPost(Post post){
        return postImgRepository.findAllByPost(post);
    }

}

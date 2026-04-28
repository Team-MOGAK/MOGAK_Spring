package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.GetAllNetworkDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.GetPostDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkListDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkPostDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.PostListDto;
import com.mogak.spring.web.dto.commentdto.CommentResponseDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MogakRepository mogakRepository;
    private final DailyJogakRepository dailyJogakRepository;
    private final UserRepository userRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final StorageCleanupService storageCleanupService;
    private static final String DIR_NAME = "img";
    private static final String ACTIVE_POST_DAILY_JOGAK_UNIQUE_INDEX = "uq_post_active_daily_jogak";

    /**
     * TODO 회고록 - user id로 조회되도록 수정
     */

    @Override
    public void validateCreateAccess(Long userId, PostRequestDto.CreatePostDto request, List<MultipartFile> multipartFile, Long jogakId) {
        validateContents(request);
        validateSourceImages(multipartFile);
        DailyJogak dailyJogak = getOwnedDailyJogak(userId, jogakId, request.targetDate());
        validateTargetDate(dailyJogak.getJogak(), dailyJogak.getTargetDate());
        if (postRepository.existsByDailyJogakIdAndDeletedAtIsNull(dailyJogak.getId())) {
            throw new PostException(ErrorCode.ALREADY_EXISTS_POST);
        }
    }

    //회고록 & 회고록 이미지 생성 => 리팩토링 필요
    @Transactional
    @Override
    public Post create(Long userId, PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList, Long jogakId) {
        validateContents(request);
        validateCreatedImages(postImgDtoList);
        DailyJogak dailyJogak = getOwnedDailyJogak(userId, jogakId, request.targetDate());
        validateTargetDate(dailyJogak.getJogak(), request.targetDate());
        if (postRepository.existsByDailyJogakIdAndDeletedAtIsNull(dailyJogak.getId())) {
            throw new PostException(ErrorCode.ALREADY_EXISTS_POST);
        }
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Post post = Post.create(dailyJogak, user, request.contents());
        for (PostImgRequestDto.CreatePostImgDto postImgDto : postImgDtoList) {
            PostImg postImg = PostImg.create(post, postImgDto.imgName(), postImgDto.imgUrl());
            //썸네일이미지인지 체크 필요
            if (postImgDto.thumbnail()) {
                post.putPostThumbnailUrl(postImg.getImgUrl()); //썸네일 이미지는 thumbnailurl에 추가
            } else {//이미지 업로드 체크
                post.putPostImg(postImg);
            }
            postImgRepository.save(postImg);
        }
        return savePost(post);
    }

    private Post savePost(Post post) {
        try {
            return postRepository.saveAndFlush(post);
        } catch (DataIntegrityViolationException e) {
            if (isActiveDailyJogakPostDuplicate(e)) {
                throw new PostException(ErrorCode.ALREADY_EXISTS_POST);
            }
            throw e;
        }
    }

    private boolean isActiveDailyJogakPostDuplicate(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause() == null ? e.getMessage() : e.getMostSpecificCause().getMessage();
        return message != null
                && message.toLowerCase(Locale.ROOT).contains(ACTIVE_POST_DAILY_JOGAK_UNIQUE_INDEX);
    }

    //회고록 조회 - 무한 스크롤
    @Override
    public PostListDto getAllPosts(Long userId, int page, Long mogakId, int size) {
        getOwnedMogak(userId, mogakId);
        Pageable pageable = PageRequest.of(page, size);

        Slice<GetPostDto> posts = postRepository.findAllPosts(mogakId, pageable)
                .map(GetPostDto::from);
        return PostListDto.of(posts.getContent(), page, size, posts.hasNext());
    }

    //회고록 상세 조회 + 댓글, 이미지 같이 보이게
    @Override
    public Post findById(Long userId, Long postId) {
        return getOwnedPost(userId, postId);
    }

    @Override
    public Post getByJogakAndTargetDate(Long userId, Long jogakId, LocalDate targetDate) {
        DailyJogak dailyJogak = getOwnedDailyJogak(userId, jogakId, targetDate);
        return postRepository.findActiveByDailyJogakId(dailyJogak.getId())
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
    }


    //회고록 수정
    @Transactional
    @Override
    public Post update(Long userId, Long postId, PostRequestDto.UpdatePostDto request) {
        Post post = getOwnedPost(userId, postId);
        validateContents(request);
        post.updatePost(request.contents());
        return post;
    }

    //회고록 삭제
    @Transactional
    @Override
    public void delete(Long userId, Long postId) {
        Post post = getOwnedPost(userId, postId);
        postLikeRepository.deleteAllByPost(post);
        List<PostImg> postImgList = postImgRepository.findAllByPost(post);
        if (!postImgList.isEmpty()) {
            storageCleanupService.deletePostImagesAfterCommit(postImgList, DIR_NAME);
            postImgRepository.deleteAllByPost(post);
        }
        postCommentRepository.findActiveAllByPostForCleanup(post).forEach(comment -> {
            comment.delete();
            post.subtractCommentCnt();
        });
        post.delete();
    }

    @Override
    public List<NetworkPostDto> getPacemakerPosts(Long userId, int cursor, int size) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Pageable pageable = PageRequest.of(cursor, size);
        List<Post> posts = postRepository.findPacemakerPostsByUser(user, pageable);
        List<Long> postIds = extractPostIds(posts);
        Map<Long, List<PostImg>> imagesByPostId = groupImagesByPostId(postIds);
        Map<Long, List<PostComment>> commentsByPostId = groupCommentsByPostId(postIds);

        return posts.stream()
                .map(p -> {
                    List<String> imgUrls = findNotThumbnailImgUrls(p, imagesByPostId);
                    List<CommentResponseDto.NetworkCommentDto> comments = commentsByPostId
                            .getOrDefault(p.getId(), Collections.emptyList()).stream()
                            .map(CommentResponseDto.NetworkCommentDto::from)
                            .collect(Collectors.toList());
                    return NetworkPostDto.from(
                            p,
                            UserResponseDto.UserDto.from(p.getUser()),
                            imgUrls,
                            comments
                    );
                })
                .collect(Collectors.toList());
    }

    //전체 네트워킹 조회 - 이미지 썸네일 제외 반환
    @Override
    public NetworkListDto getNetworkPosts(Long userId, int page, int size, String sort, String address /*List<String> categoryList,*/){
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if(address == null){
            address = user.getAddress().getName();
        }
        Pageable pageable = PageRequest.of(page, size);
        Slice<Post> posts = postRepository.findNetworkPosts(address, sort, pageable);
        Map<Long, List<PostImg>> imagesByPostId = groupImagesByPostId(extractPostIds(posts.getContent()));
        List<GetAllNetworkDto> postDtos = posts.map(post -> GetAllNetworkDto.from(post, findNotThumbnailImgUrls(post, imagesByPostId)))
                .getContent();
        return NetworkListDto.of(postDtos, page, size, posts.hasNext());
    }

    private List<Long> extractPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, List<PostImg>> groupImagesByPostId(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return postImgRepository.findAllByPostIdIn(postIds).stream()
                .collect(Collectors.groupingBy(postImg -> postImg.getPost().getId()));
    }

    private Map<Long, List<PostComment>> groupCommentsByPostId(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return postCommentRepository.findActiveAllByPostIdInWithUser(postIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getPost().getId()));
    }

    private List<String> findNotThumbnailImgUrls(Post post, Map<Long, List<PostImg>> imagesByPostId) {
        return imagesByPostId.getOrDefault(post.getId(), Collections.emptyList()).stream()
                .filter(img -> !Objects.equals(img.getImgUrl(), post.getPostThumbnailUrl()))
                .map(PostImg::getImgUrl)
                .collect(Collectors.toList());
    }

    /*
    이미지 관련 함수들
     */
    //postId로 해당 회고록에 대한 이미지 url 반환
    @Override
    public List<String> findImgUrlByPost(Long postId){
        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        List<PostImg> postImgList = postImgRepository.findAllByPost(post);
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg :postImgList) {
            imgUrlList.add(postImg.getImgUrl());
        }
        return imgUrlList;
    }

    //회고록 상세조회를 위한 활성 댓글 id 반환
    @Override
    public List<Long> findActiveCommentIds(Post post) {
        return postCommentRepository.findActiveAllByPost(post).stream()
                .map(PostComment::getId)
                .collect(Collectors.toList());
    }

    //이미지 상세조회를 위한, 썸네일 제외 url 반환
    @Override
    public List<String> findNotThumbnailImg(Post post) {
        String thumbnailUrl = post.getPostThumbnailUrl();
        List<PostImg> postImgList = postImgRepository.findAllByPost(post);
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

    private Mogak getMogak(Long mogakId) {
        return mogakRepository.findActiveById(mogakId).orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
    }

    private Mogak getOwnedMogak(Long userId, Long mogakId) {
        Mogak mogak = getMogak(mogakId);
        validateOwner(mogak.getUser().getId(), userId);
        return mogak;
    }

    private Post getOwnedPost(Long userId, Long postId) {
        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        validateOwner(post.getUser().getId(), userId);
        return post;
    }

    private DailyJogak getOwnedDailyJogak(Long userId, Long jogakId, LocalDate targetDate) {
        if (targetDate == null) {
            throw new PostException(ErrorCode.INVALID_TARGET_DATE);
        }
        DailyJogak dailyJogak = dailyJogakRepository.findActiveByJogakIdAndTargetDateWithJogakGraph(jogakId, targetDate)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_DAILY_JOGAK));
        validateOwner(dailyJogak.getJogak().getUser().getId(), userId);
        return dailyJogak;
    }

    private void validateTargetDate(Jogak jogak, LocalDate targetDate) {
        if (jogak.getStartAt() == null
                || targetDate.isBefore(jogak.getStartAt())
                || (jogak.getEndAt() != null && targetDate.isAfter(jogak.getEndAt()))) {
            throw new PostException(ErrorCode.INVALID_TARGET_DATE);
        }
        if (jogak.getIsRoutine()) {
            int dayOfWeek = targetDate.getDayOfWeek().getValue();
            boolean containsTargetDay = jogak.getJogakPeriods().stream()
                    .map(JogakPeriod::getPeriod)
                    .anyMatch(period -> period.getId() == dayOfWeek);
            if (!containsTargetDay) {
                throw new PostException(ErrorCode.INVALID_TARGET_DATE);
            }
        }
    }

    private void validateContents(PostRequestDto.CreatePostDto request) {
        if (request == null || request.contents() == null) {
            throw new PostException(ErrorCode.INVALID_PARAMETER_ERROR);
        }
        if (request.contents().length() > 350) {
            throw new PostException(ErrorCode.EXCEED_MAX_NUM_POST);
        }
    }

    private void validateContents(PostRequestDto.UpdatePostDto request) {
        if (request == null || request.contents() == null) {
            throw new PostException(ErrorCode.INVALID_PARAMETER_ERROR);
        }
        if (request.contents().length() > 350) {
            throw new PostException(ErrorCode.EXCEED_MAX_NUM_POST);
        }
    }

    private void validateSourceImages(List<MultipartFile> multipartFile) {
        if (multipartFile == null || multipartFile.stream().allMatch(MultipartFile::isEmpty)) {
            throw new PostException(ErrorCode.NOT_HAVE_IMAGE);
        }
    }

    private void validateCreatedImages(List<PostImgRequestDto.CreatePostImgDto> postImgDtoList) {
        if (postImgDtoList == null || postImgDtoList.isEmpty()) {
            throw new PostException(ErrorCode.NOT_HAVE_IMAGE);
        }
    }

    private void validateOwner(Long ownerUserId, Long userId) {
        if (!Objects.equals(ownerUserId, userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
    }

}

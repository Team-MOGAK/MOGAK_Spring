package com.mogak.spring.integration;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.DailyJogakStatus;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.DailyJogakRepository;
import com.mogak.spring.repository.FollowRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.JogakPeriodRepository;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.ModaratRepository;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.PostCommentRepository;
import com.mogak.spring.repository.PostImgRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.service.PostCommentService;
import com.mogak.spring.service.PostService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.GetAllNetworkDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.GetPostDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkListDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkPostDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.PostListDto;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ListQueryCountIntegrationTest {

    @Autowired private PostService postService;
    @Autowired private MogakService mogakService;
    @Autowired private PostCommentService postCommentService;

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private ModaratRepository modaratRepository;
    @Autowired private MogakCategoryRepository categoryRepository;
    @Autowired private MogakRepository mogakRepository;
    @Autowired private JogakRepository jogakRepository;
    @Autowired private DailyJogakRepository dailyJogakRepository;
    @Autowired private JogakPeriodRepository jogakPeriodRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PostImgRepository postImgRepository;
    @Autowired private PostCommentRepository postCommentRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private EntityManager entityManager;

    @MockitoBean private AppleOAuthUserProvider appleOAuthUserProvider;
    @MockitoBean private StorageService storageService;

    private Statistics statistics;
    private Job job;
    private Address address;
    private MogakCategory category;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        statistics = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();
        statistics.setStatisticsEnabled(true);
        job = jobRepository.save(TestFixtureFactory.job("개발/데이터"));
        address = addressRepository.save(TestFixtureFactory.address("서울특별시"));
        category = categoryRepository.save(TestFixtureFactory.category(null, "자격증"));
        today = LocalDate.now();
    }

    @Test
    @DisplayName("네트워크 게시글 목록은 게시글 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void networkPostsQueryCountDoesNotGrowWithPostCount() {
        User viewer = saveUser("viewer-network@test.com", "viewer");
        User writer = saveUser("writer-network@test.com", "writer");
        Mogak mogak = saveMogak(writer, "네트워크 모각");
        savePost(writer, mogak, "one", "thumb-one");
        savePost(writer, mogak, "two", "thumb-two");
        savePost(writer, mogak, "three", "thumb-three");
        flushAndClear();

        QueryResult<NetworkListDto> singlePostResult = countQueries(
                () -> postService.getNetworkPosts(viewer.getId(), 0, 1, "createdAt", address.getName())
        );
        QueryResult<NetworkListDto> threePostResult = countQueries(
                () -> postService.getNetworkPosts(viewer.getId(), 0, 3, "createdAt", address.getName())
        );

        assertThat(singlePostResult.value().items())
                .extracting(GetAllNetworkDto::contents)
                .containsExactly("three");
        assertThat(threePostResult.value().items())
                .extracting(GetAllNetworkDto::contents)
                .containsExactly("three", "two", "one");
        assertThat(threePostResult.value().items())
                .allSatisfy(post -> {
                    assertThat(post.userName()).isEqualTo("writer");
                    assertThat(post.userJob()).isEqualTo(job.getName());
                    assertThat(post.imgUrls()).hasSize(1);
                });
        assertThat(threePostResult.queryCount()).isEqualTo(singlePostResult.queryCount());
    }

    @Test
    @DisplayName("페이스메이커 게시글 목록은 이미지와 댓글 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void pacemakerPostsQueryCountDoesNotGrowWithPostCount() {
        User viewer = saveUser("viewer-pace@test.com", "viewer");
        User writer = saveUser("writer-pace@test.com", "writer");
        followRepository.save(Follow.builder()
                .fromUser(viewer)
                .toUser(writer)
                .build());
        Mogak mogak = saveMogak(writer, "페이스메이커 모각");
        savePost(writer, mogak, "one", "thumb-one");
        savePost(writer, mogak, "two", "thumb-two");
        savePost(writer, mogak, "three", "thumb-three");
        flushAndClear();

        QueryResult<List<NetworkPostDto>> singlePostResult = countQueries(
                () -> postService.getPacemakerPosts(viewer.getId(), 0, 1)
        );
        QueryResult<List<NetworkPostDto>> threePostResult = countQueries(
                () -> postService.getPacemakerPosts(viewer.getId(), 0, 3)
        );

        assertThat(singlePostResult.value()).hasSize(1);
        assertThat(threePostResult.value())
                .extracting(NetworkPostDto::contents)
                .containsExactly("three", "two", "one");
        assertThat(threePostResult.value())
                .allSatisfy(post -> {
                    assertThat(post.user().nickname()).isEqualTo("writer");
                    assertThat(post.user().job()).isEqualTo(job.getName());
                    assertThat(post.imgUrls()).hasSize(1);
                    assertThat(post.comments()).hasSize(1);
                });
        assertThat(threePostResult.queryCount()).isEqualTo(singlePostResult.queryCount());
    }

    @Test
    @DisplayName("모각별 게시글 목록은 게시글 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void mogakPostsQueryCountDoesNotGrowWithPostCount() {
        User writer = saveUser("writer-mogak-post@test.com", "writer");
        Mogak mogak = saveMogak(writer, "게시글 모각");
        savePost(writer, mogak, "one", "thumb-one");
        savePost(writer, mogak, "two", "thumb-two");
        savePost(writer, mogak, "three", "thumb-three");
        flushAndClear();

        QueryResult<PostListDto> singlePostResult = countQueries(
                () -> postService.getAllPosts(writer.getId(), 0, mogak.getId(), 1)
        );
        QueryResult<PostListDto> threePostResult = countQueries(
                () -> postService.getAllPosts(writer.getId(), 0, mogak.getId(), 3)
        );

        assertThat(singlePostResult.value().items())
                .extracting(GetPostDto::contents)
                .containsExactly("three");
        assertThat(threePostResult.value().items())
                .extracting(GetPostDto::contents)
                .containsExactly("three", "two", "one");
        assertThat(threePostResult.value().items())
                .allSatisfy(post -> {
                    assertThat(post.mogakId()).isEqualTo(mogak.getId());
                    assertThat(post.thumbnailUrl()).startsWith("https://example.com/thumb-");
                });
        assertThat(threePostResult.queryCount()).isEqualTo(singlePostResult.queryCount());
    }

    @Test
    @DisplayName("모각 목록은 모각 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void mogakListQueryCountDoesNotGrowWithMogakCount() {
        User writer = saveUser("writer-mogak-list@test.com", "writer");
        Modarat smallModarat = modaratRepository.save(TestFixtureFactory.modarat(null, writer, "작은 모다라트", "#111111"));
        Modarat largeModarat = modaratRepository.save(TestFixtureFactory.modarat(null, writer, "큰 모다라트", "#222222"));
        mogakRepository.save(TestFixtureFactory.mogak(null, writer, smallModarat, category, "small-one", "#111111"));
        mogakRepository.save(TestFixtureFactory.mogak(null, writer, largeModarat, category, "large-one", "#111111"));
        mogakRepository.save(TestFixtureFactory.mogak(null, writer, largeModarat, category, "large-two", "#222222"));
        mogakRepository.save(TestFixtureFactory.mogak(null, writer, largeModarat, category, "large-three", "#333333"));
        flushAndClear();

        QueryResult<MogakResponseDto.GetMogakListDto> singleMogakResult = countQueries(
                () -> mogakService.getMogakDtoList(writer.getId(), smallModarat.getId())
        );
        QueryResult<MogakResponseDto.GetMogakListDto> threeMogakResult = countQueries(
                () -> mogakService.getMogakDtoList(writer.getId(), largeModarat.getId())
        );

        assertThat(singleMogakResult.value().mogaks())
                .extracting(MogakResponseDto.GetMogakDto::title)
                .containsExactly("small-one");
        assertThat(threeMogakResult.value().mogaks())
                .extracting(MogakResponseDto.GetMogakDto::title)
                .containsExactlyInAnyOrder("large-one", "large-two", "large-three");
        assertThat(threeMogakResult.value().mogaks())
                .allSatisfy(mogak -> assertThat(mogak.bigCategory().getName()).isEqualTo(category.getName()));
        assertThat(threeMogakResult.queryCount()).isEqualTo(singleMogakResult.queryCount());
    }

    @Test
    @DisplayName("조각 목록은 조각과 요일 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void jogakListQueryCountDoesNotGrowWithJogakCount() {
        User user = saveUser("writer-jogak@test.com", "writer");
        Mogak smallMogak = saveMogak(user, "작은 조각 모각");
        Mogak largeMogak = saveMogak(user, "큰 조각 모각");
        saveJogakWithPeriod(user, smallMogak, "small-one", "MONDAY");
        saveJogakWithPeriod(user, largeMogak, "large-one", "MONDAY");
        saveJogakWithPeriod(user, largeMogak, "large-two", "TUESDAY");
        saveJogakWithPeriod(user, largeMogak, "large-three", "WEDNESDAY");
        flushAndClear();

        QueryResult<List<JogakResponseDto.GetJogakDto>> singleJogakResult = countQueries(
                () -> mogakService.getJogaks(user.getId(), smallMogak.getId(), today)
        );
        QueryResult<List<JogakResponseDto.GetJogakDto>> threeJogakResult = countQueries(
                () -> mogakService.getJogaks(user.getId(), largeMogak.getId(), today)
        );

        assertThat(singleJogakResult.value())
                .extracting(JogakResponseDto.GetJogakDto::title)
                .containsExactly("small-one");
        assertThat(threeJogakResult.value())
                .extracting(JogakResponseDto.GetJogakDto::title)
                .containsExactlyInAnyOrder("large-one", "large-two", "large-three");
        assertThat(threeJogakResult.value())
                .allSatisfy(jogak -> {
                    assertThat(jogak.mogakTitle()).isEqualTo(largeMogak.getTitle());
                    assertThat(jogak.category()).isEqualTo(category.getName());
                    assertThat(jogak.days()).hasSize(1);
                });
        assertThat(threeJogakResult.queryCount()).isEqualTo(singleJogakResult.queryCount());
    }

    @Test
    @DisplayName("댓글 목록은 댓글 수가 늘어도 쿼리 수가 선형 증가하지 않는다")
    void commentListQueryCountDoesNotGrowWithCommentCount() {
        User writer = saveUser("writer-comment@test.com", "writer");
        User commenter = saveUser("commenter@test.com", "commenter");
        Mogak mogak = saveMogak(writer, "댓글 모각");
        Post smallPost = savePost(writer, mogak, "one", "thumb-one", false);
        Post largePost = savePost(writer, mogak, "two", "thumb-two", false);
        saveComment(smallPost, commenter, "small-one");
        saveComment(largePost, commenter, "large-one");
        saveComment(largePost, commenter, "large-two");
        saveComment(largePost, commenter, "large-three");
        flushAndClear();

        QueryResult<List<PostComment>> singleCommentResult = countQueries(
                () -> postCommentService.findByPostId(smallPost.getId())
        );
        QueryResult<List<PostComment>> threeCommentResult = countQueries(
                () -> postCommentService.findByPostId(largePost.getId())
        );

        assertThat(singleCommentResult.value())
                .extracting(PostComment::getContents)
                .containsExactly("small-one");
        assertThat(threeCommentResult.value())
                .extracting(PostComment::getContents)
                .containsExactlyInAnyOrder("large-one", "large-two", "large-three");
        assertThat(threeCommentResult.value())
                .allSatisfy(comment -> {
                    assertThat(comment.getPost().getId()).isEqualTo(largePost.getId());
                    assertThat(comment.getUser().getNickname()).isEqualTo("commenter");
                });
        assertThat(threeCommentResult.queryCount()).isEqualTo(singleCommentResult.queryCount());
    }

    private <T> QueryResult<T> countQueries(QueryAction<T> action) {
        statistics.clear();
        T value = action.run();
        long queryCount = statistics.getPrepareStatementCount();
        entityManager.clear();
        return new QueryResult<>(value, queryCount);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private User saveUser(String email, String nickname) {
        return userRepository.save(TestFixtureFactory.user(null, email, nickname, job, address));
    }

    private Mogak saveMogak(User user, String title) {
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, user, title + " 모다라트", "#111111"));
        return mogakRepository.save(TestFixtureFactory.mogak(null, user, modarat, category, title, "#222222"));
    }

    private Post savePost(User writer, Mogak mogak, String contents, String thumbnailName) {
        return savePost(writer, mogak, contents, thumbnailName, true);
    }

    private Post savePost(User writer, Mogak mogak, String contents, String thumbnailName, boolean includeComment) {
        Jogak jogak = jogakRepository.save(TestFixtureFactory.jogak(null, mogak, contents + " 조각", false, today, null, 0));
        DailyJogak dailyJogak = dailyJogakRepository.save(TestFixtureFactory.dailyJogak(null, jogak, today, DailyJogakStatus.SUCCESS));
        Post post = postRepository.save(Post.builder()
                .dailyJogak(dailyJogak)
                .user(writer)
                .contents(contents)
                .postThumbnailUrl("https://example.com/" + thumbnailName + ".png")
                .viewCnt(0)
                .build());
        postImgRepository.save(PostImg.builder()
                .post(post)
                .imgName(thumbnailName + ".png")
                .imgUrl("https://example.com/" + thumbnailName + ".png")
                .build());
        postImgRepository.save(PostImg.builder()
                .post(post)
                .imgName(contents + ".png")
                .imgUrl("https://example.com/" + contents + ".png")
                .build());
        if (includeComment) {
            saveComment(post, writer, contents + " 댓글");
        }
        return post;
    }

    private void saveComment(Post post, User user, String contents) {
        postCommentRepository.save(PostComment.builder()
                .post(post)
                .user(user)
                .contents(contents)
                .build());
    }

    private void saveJogakWithPeriod(User user, Mogak mogak, String title, String day) {
        Jogak jogak = jogakRepository.save(TestFixtureFactory.jogak(null, mogak, title, true, today, null, 0));
        Period period = TestFixtureFactory.period(day);
        entityManager.persist(period);
        jogakPeriodRepository.save(TestFixtureFactory.jogakPeriod(jogak, period));
        dailyJogakRepository.save(TestFixtureFactory.dailyJogak(null, jogak, today, DailyJogakStatus.PENDING));
    }

    @FunctionalInterface
    private interface QueryAction<T> {
        T run();
    }

    private record QueryResult<T>(T value, long queryCount) {
    }
}

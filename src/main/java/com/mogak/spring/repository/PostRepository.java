package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    //회고록 전체 조회 - 무한스크롤 - 모각 id가 _일경우 id순 조회
    //slice 사용해 별도의 카운트 쿼리를 호출하지 않고 원래 갯수보다 1개 더 불러와 다음에 조회할 회고록 있는지 확인할 수 있음
    //fetch join시 where 절에 join의 대상에 대한 조건 써도 될까?
    @Query("select p from Post p " +
            "join p.dailyJogak dj join dj.jogak j join j.mogak m join p.user u " +
            "where m.id = :mogakId and p.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null " +
            "order by p.id desc")
    Slice<Post> findAllPosts(@Param("mogakId")Long mogakId, Pageable pageable);

    @Query("select p from Post p " +
            "join p.dailyJogak dj join dj.jogak j join j.mogak m join p.user u " +
            "where p.id = :postId and p.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null")
    Optional<Post> findActiveById(@Param("postId") Long postId);

    @Query("select p from Post p " +
            "join p.dailyJogak dj join dj.jogak j join j.mogak m join p.user u " +
            "where dj.id = :dailyJogakId and p.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null")
    Optional<Post> findActiveByDailyJogakId(@Param("dailyJogakId") Long dailyJogakId);

    @Query("select p from Post p " +
            "join p.dailyJogak dj join dj.jogak j join j.mogak m join p.user u " +
            "where dj.id = :dailyJogakId and p.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null")
    List<Post> findActiveAllByDailyJogakId(@Param("dailyJogakId") Long dailyJogakId);

    boolean existsByDailyJogakIdAndDeletedAtIsNull(Long dailyJogakId);

    @Query("select p from Post p " +
            "join p.dailyJogak dj join dj.jogak j join j.mogak m join p.user u " +
            "where u.id = :userId and p.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null")
    List<Post> findActiveAllByUserId(@Param("userId") Long userId);

    @Query( "SELECT p " +
            "FROM Post p JOIN Follow f ON p.user = f.toUser JOIN p.user u " +
            "JOIN p.dailyJogak dj JOIN dj.jogak j JOIN j.mogak m " +
            "WHERE f.fromUser = :user and p.deletedAt is null and u.deletedAt is null " +
            "and f.fromUser.deletedAt is null and dj.deletedAt is null " +
            "and j.deletedAt is null and m.deletedAt is null " +
            "ORDER BY p.id DESC")
    List<Post> findPacemakerPostsByUser(@Param("user") User user, Pageable pageable);

    //네트워킹 전체 조회 - 거주지,최근순,인기순만 변경 가능 & 카테고리는 기본적으로 다, 직무도 다
    /*
    @Query("SELECT p FROM Post p WHERE p.user.address.name = :address AND p.user.job.name = :job ORDER BY " +
            "CASE WHEN :sort = 'createdAt' THEN p.createdAt END DESC, "
            + "CASE WHEN :sort = 'likeCnt' THEN p.likeCnt END DESC")

     */
    @Query("SELECT p FROM Post p JOIN FETCH p.user u " +
            "JOIN p.dailyJogak dj JOIN dj.jogak j JOIN j.mogak m " +
            "WHERE u.address.name = :address AND p.deletedAt is null AND u.deletedAt is null " +
            "AND dj.deletedAt is null AND j.deletedAt is null AND m.deletedAt is null ORDER BY "
            + "CASE WHEN :sort = 'createdAt' THEN p.createdAt END DESC, "
            + "CASE WHEN :sort = 'likeCnt' THEN p.likeCnt END DESC" )
    Slice<Post> findNetworkPosts(@Param("address") String address, @Param("sort") String sort, Pageable pageable);

}

package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    //회고록 전체 조회 - 무한스크롤 - 모각 id가 _일경우 id순 조회
    //slice 사용해 별도의 카운트 쿼리를 호출하지 않고 원래 갯수보다 1개 더 불러와 다음에 조회할 회고록 있는지 확인할 수 있음
    //fetch join시 where 절에 join의 대상에 대한 조건 써도 될까?
    @Query("select p from Post p where p.mogak.id = :mogakId order by p.id desc")
    Slice<Post> findAllPosts(@Param("mogakId")Long mogakId, Pageable pageable);

    void deleteAllByMogak(Mogak mogak);

    List<Post> findAllByMogak(Mogak mogak);

    @Query( "SELECT p " +
            "FROM Post p JOIN Follow f ON p.user = f.toUser " +
            "WHERE f.fromUser = :user " +
            "ORDER BY p.id DESC")
    List<Post> findPacemakerPostsByUser(@Param("user") User user, Pageable pageable);

    //네트워킹 전체 조회 - 거주지,최근순,인기순만 변경 가능 & 카테고리는 기본적으로 다, 직무도 다
    /*
    @Query("SELECT p FROM Post p WHERE p.user.address.name = :address AND p.user.job.name = :job ORDER BY " +
            "CASE WHEN :sort = 'createdAt' THEN p.createdAt END DESC, "
            + "CASE WHEN :sort = 'likeCnt' THEN p.likeCnt END DESC")

     */
    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE u.address.name = :address ORDER BY "
            + "CASE WHEN :sort = 'createdAt' THEN p.createdAt END DESC, "
            + "CASE WHEN :sort = 'likeCnt' THEN p.likeCnt END DESC" )
    Slice<Post> findNetworkPosts(@Param("address") String address, @Param("sort") String sort, Pageable pageable);

}

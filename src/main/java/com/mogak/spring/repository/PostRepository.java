package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    //회고록 전체 조회 - 무한스크롤 - 모각 id가 _일경우 id순 조회
    //slice 사용해 별도의 카운트 쿼리를 호출하지 않고 원래 갯수보다 1개 더 불러와 다음에 조회할 회고록 있는지 확인할 수 있음
    //fetch join시 where 절에 join의 대상에 대한 조건 써도 될까?
    @Query("select p from Post p join p.mogak m on m.id = :mogakId where p.id < :postId order by p.id desc")
    Slice<Post> findAllPosts(@Param("postId") Long lastPostId, @Param("mogakId")Long mogakId, Pageable pageable);

    void deleteAllByMogak(Mogak mogak);

    List<Post> findAllByMogak(Mogak mogak);
}

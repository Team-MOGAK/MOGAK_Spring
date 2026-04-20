package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    List<PostImg> findAllByPost(Post post);
    void deleteAllByPost(Post post);

    @Query("select pi from PostImg pi where pi.post.user.id = :userId")
    List<PostImg> findAllByPostOwnerId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from PostImg pi where pi.post.user.id = :userId")
    void deleteAllByPostOwnerId(@Param("userId") Long userId);
}

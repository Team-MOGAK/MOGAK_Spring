package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);

    @Modifying
    @Query("delete from PostLike pl where pl.post = :post")
    void deleteAllByPost(@Param("post") Post post);

    @Query("select pl from PostLike pl join pl.post p join p.user pu " +
            "where pl.user.id = :userId and pu.id <> :userId and p.deletedAt is null and pu.deletedAt is null")
    List<PostLike> findActiveAllByUserIdOnOtherUserPosts(@Param("userId") Long userId);

    @Modifying
    @Query("delete from PostLike pl where pl.user.id = :userId or pl.post.user.id = :userId")
    void deleteAllRelatedToUser(@Param("userId") Long userId);
}

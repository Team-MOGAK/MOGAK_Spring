package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    //회고록 id&댓글 id로 댓글 한개 조회
    @Query("select pc from PostComment pc where pc.post = :post and pc.id = :commentId and pc.deletedAt is null")
    PostComment findActiveByPostAndId(@Param("post") Post post, @Param("commentId") Long commentId);

    @Deprecated
    default PostComment findByPostAndId(Post post, Long commentId) {
        return findActiveByPostAndId(post, commentId);
    }

    //댓글 여러개 조회
    @Query("select pc from PostComment pc join pc.user u where pc.post = :post and pc.deletedAt is null and u.deletedAt is null")
    List<PostComment> findActiveAllByPost(@Param("post") Post post);

    @Query("select pc from PostComment pc where pc.user.id = :userId and pc.deletedAt is null")
    List<PostComment> findActiveAllByUserId(@Param("userId") Long userId);

    @Query("select pc from PostComment pc where pc.post.user.id = :userId and pc.deletedAt is null")
    List<PostComment> findActiveAllByPostOwnerId(@Param("userId") Long userId);
}

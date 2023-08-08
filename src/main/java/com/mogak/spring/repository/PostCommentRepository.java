package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    //회고록 id&댓글 id로 댓글 한개 조회
    PostComment findByPostAndId(Long postId, Long commentId);

    //댓글 여러개 조회
    List<PostComment> findAllByPost(Long postId);

    //댓글 삭제
    void deleteByPostAndId(Long postId, Long commentId);
    //연쇄삭제
    void deleteAllByPost(Post post);
}

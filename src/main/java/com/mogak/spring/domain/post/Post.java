package com.mogak.spring.domain.post;

import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Table(name = "post")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mogak_id")
    private Mogak mogak;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Column(nullable = false)
    private String contents;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogak_id")
    private Jogak jogak;
    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<PostComment> postComments = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<PostImg> postImgs = new ArrayList<>();
    @Column(nullable = false)
    private String postThumbnailUrl;
    @Column(nullable = false)
    private String validation;
    @Column(nullable = false)
    private int viewCnt;
    @Builder.Default
    @Column(nullable = false)
    private int likeCnt = 0;
    @Builder.Default
    @Column(nullable = false)
    private int commentCnt = 0; //데이터 일관성 문제 있을수도

    //setter 대신
    public void updatePost(String contents){
        this.contents=contents;
    }

    public void addPostLike(){ this.likeCnt= this.likeCnt+1; }

    public void subtractPostLike(){
        this.likeCnt=this.likeCnt-1;
    }

    public void putComment(PostComment postComment){
        this.postComments.add(postComment);
    }

    public void putPostImg(PostImg postImg){
        this.postImgs.add(postImg);
    }

    public void putPostThumbnailUrl(String thumbnailUrl){
        this.postThumbnailUrl=thumbnailUrl;
    }

    public void addCommentCnt(){ this.commentCnt+=1;}
    public void subtractCommentCnt(){ this.commentCnt-=1;}


}

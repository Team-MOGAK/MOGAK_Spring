package com.mogak.spring.domain.post;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "post_comment")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String validation;

    public void updateComment(String contents){
        this.contents=contents;
    }
}

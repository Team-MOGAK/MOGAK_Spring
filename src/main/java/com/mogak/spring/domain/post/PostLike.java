package com.mogak.spring.domain.post;

import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Table(
        name = "post_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_post_like_post_user",
                columnNames = {"post_id", "user_id"}
        )
)
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    public static PostLike of(Post post, User user) {
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }
}

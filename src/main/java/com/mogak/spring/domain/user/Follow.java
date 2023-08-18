package com.mogak.spring.domain.user;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "follow")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private User toUser;
}

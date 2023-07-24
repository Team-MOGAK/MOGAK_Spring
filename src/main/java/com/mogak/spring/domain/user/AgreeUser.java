package com.mogak.spring.domain.user;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "user_agree")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgreeUser extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agree_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Column(nullable = false)
    private Boolean requireAgree;
    @Column(nullable = false)
    private Boolean optionalAgree;
}

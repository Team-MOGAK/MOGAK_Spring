package com.mogak.spring.domain.user;

import com.mogak.spring.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "users")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String nickname;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id")
    private Job job;
    private Character gender;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    private String profileImg;
    private String email;
    private Double weekRate;
    @Column(nullable = false)
    private String validation;
    
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}

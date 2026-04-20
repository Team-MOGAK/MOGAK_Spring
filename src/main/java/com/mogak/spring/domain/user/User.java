package com.mogak.spring.domain.user;

import com.mogak.spring.global.SoftDeletableEntity;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Table(name = "users")
@Entity
//@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
//@Where(clause = "deleted=false")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    //@Column(nullable = false)
    private String nickname;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;
    private Character gender;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    private String profileImgUrl;
    private String profileImgName;
    private String email;
    @Column(length = 512)
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;


    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateJob(Job job) {
        this.job = job;
    }

    public void updateProfileImg(String imgUrl, String imgName) {
        this.profileImgUrl = imgUrl;
        this.profileImgName = imgName;
    }

    public User(String email) {
        this.email = email;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public void registerUser(String nickname, Job job, Address address, String profileImgUrl, String profileImgName) {
        this.nickname = nickname;
        this.job = job;
        this.address = address;
        this.profileImgUrl = profileImgUrl;
        this.profileImgName = profileImgName;
        this.role = Role.USER;
    }
}

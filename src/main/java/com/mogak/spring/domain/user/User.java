package com.mogak.spring.domain.user;

import com.mogak.spring.global.BaseEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "users")
@Entity
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
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
    //@Column(nullable = false)
    private String validation;
    private boolean deleted = Boolean.FALSE;


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

    public void updateValidation(String validation) {
        this.validation = validation;
    }

    public void registerUser(String nickname, Job job, Address address, String profileImgUrl, String profileImgName) {
        this.nickname = nickname;
        this.job = job;
        this.address = address;
        this.profileImgUrl = profileImgUrl;
        this.profileImgName = profileImgName;
        this.validation = "ACTIVE";
    }
}

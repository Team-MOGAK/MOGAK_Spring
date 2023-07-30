package com.mogak.spring.domain.user;

import com.mogak.spring.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "user")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id")
    private Job job;
    @Column(nullable = false)
    private String nickname;
    private char gender;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    private String profileImg;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String validation;
    private Double weekRate;

}

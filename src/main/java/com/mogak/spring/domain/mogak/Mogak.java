package com.mogak.spring.domain.mogak;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Table(name = "mogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogak extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mogak_category_id")
    private MogakCategory category;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private LocalDate startAt;
    private LocalDate endAt;
    @Column(nullable = false)
    private String validation;
}

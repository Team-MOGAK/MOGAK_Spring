package com.mogak.spring.domain.notice;

import com.mogak.spring.global.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice_img")
@Entity
public class NoticeImg extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_img_id")
    private Long id;
    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="notice_id")
    private Notice noticeId;
}

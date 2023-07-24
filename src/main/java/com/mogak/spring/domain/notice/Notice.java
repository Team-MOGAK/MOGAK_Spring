package com.mogak.spring.domain.notice;

import com.mogak.spring.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice")
@Entity
public class Notice extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
}

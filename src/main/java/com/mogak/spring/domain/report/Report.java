package com.mogak.spring.domain.report;

import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "report")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="from_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_id")
    private User toUser;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String category;


}

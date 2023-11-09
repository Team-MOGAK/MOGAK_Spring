package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
@Builder
@Getter
@Table(name = "daily_jogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyJogak extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_jogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_id")
    private Mogak mogak;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_category")
    private MogakCategory category;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Boolean achievement;
    @Column(nullable = false)
    private Boolean isRoutine;

    public void updateSuccess() {
        this.achievement = true;
    }
}
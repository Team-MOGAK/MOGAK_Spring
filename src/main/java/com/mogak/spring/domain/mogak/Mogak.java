package com.mogak.spring.domain.mogak;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Column(name = "category_other")
    private String otherCategory;
    @OneToMany(mappedBy = "mogak")
    private List<MogakPeriod> mogakPeriods = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private LocalDate startAt;
    private LocalDate endAt;
    @Column(nullable = false)
    private String validation;

    public void updateState(String state) {
        this.state = state;
    }
    public void updateCategory(MogakCategory category) {
        this.category = category;
    }
    public void updateOtherCategory(String otherCategory) {
        this.otherCategory = otherCategory;
    }

    public void updateFromDto(MogakRequestDto.UpdateDto updateDto) {
        Optional.ofNullable(updateDto.getTitle()).ifPresent(title -> {
            this.title = title;
        });
        Optional.ofNullable(updateDto.getStartAt()).ifPresent(startDay -> {
            if (!this.state.equals(State.BEFORE.toString())) {
                throw new IllegalArgumentException("시작 날짜는 시작하기 전에만 수정 가능합니다");
            }
            this.state = State.registerState(startDay, endAt, LocalDate.now()).toString();
            this.startAt = startDay;
        });
        Optional.ofNullable(updateDto.getEndAt()).ifPresent(endDay -> {
            this.endAt = endDay;
        });
    }
}

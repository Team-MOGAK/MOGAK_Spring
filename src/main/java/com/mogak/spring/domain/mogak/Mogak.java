package com.mogak.spring.domain.mogak;

import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modarat_id")
    private Modarat modarat;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "big_category")
    private MogakCategory bigCategory;
    @Column(name = "small_category")
    private String smallCategory;
    @Builder.Default
    @OneToMany(mappedBy = "jogak")
    private List<JogakPeriod> jogakPeriods = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "mogak")
    private List<Jogak> jogaks = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private LocalDate startAt;
    private LocalDate endAt;
    private String color;
    @Column(nullable = false)
    private String validation;

    public List<String> getPeriod() {
        return this.getJogakPeriods()
                .stream()
                .map(JogakPeriod::getPeriod)
                .map(Period::getDays)
                .collect(Collectors.toList());
    }

    public void updateState(String state) {
        this.state = state;
    }
    public void updateBigCategory(MogakCategory category) {
        this.bigCategory = category;
    }

    public void updateFromDto(MogakRequestDto.UpdateDto updateDto) {
        Optional.ofNullable(updateDto.getTitle()).ifPresent(updateTitle -> this.title = updateTitle);
        Optional.ofNullable(updateDto.getStartAt()).ifPresent(startDay -> {
            if (!this.state.equals(State.BEFORE.toString())) {
                throw new MogakException(ErrorCode.CAN_MODIFIED_BEFORE_START_MOGAK);
            }
            this.state = State.registerState(startDay, endAt, LocalDate.now()).toString();
            this.startAt = startDay;
        });
        Optional.ofNullable(updateDto.getEndAt()).ifPresent(endDay -> this.endAt = endDay);
        Optional.ofNullable(updateDto.getSmallCategory()).ifPresent(category -> this.smallCategory = category);
        Optional.ofNullable(updateDto.getColor()).ifPresent(updateColor -> this.color = updateColor);
    }
}

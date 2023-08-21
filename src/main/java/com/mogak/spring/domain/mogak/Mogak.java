package com.mogak.spring.domain.mogak;

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
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="mogak_category_id")
    private MogakCategory category;
    @Column(name = "category_other")
    private String otherCategory;
    @Builder.Default
    @OneToMany(mappedBy = "mogak")
    private List<MogakPeriod> mogakPeriods = new ArrayList<>();
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
    @Column(nullable = false)
    private String validation;

    public List<String> getPeriod() {
        return this.getMogakPeriods()
                .stream()
                .map(MogakPeriod::getPeriod)
                .map(Period::getDays)
                .collect(Collectors.toList());
    }

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
                throw new MogakException(ErrorCode.CAN_MODIFIED_BEFORE_START_MOGAK);
            }
            this.state = State.registerState(startDay, endAt, LocalDate.now()).toString();
            this.startAt = startDay;
        });
        Optional.ofNullable(updateDto.getEndAt()).ifPresent(endDay -> {
            this.endAt = endDay;
        });
    }
}

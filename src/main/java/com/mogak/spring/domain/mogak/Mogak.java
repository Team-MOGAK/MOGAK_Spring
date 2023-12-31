package com.mogak.spring.domain.mogak;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.global.ErrorCode;
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

    public void updateState(String state) {
        this.state = state;
    }
    public void updateBigCategory(MogakCategory category) {
        this.bigCategory = category;
    }

    public void update(String title,
                       String smallCategory,
                       LocalDate startAt,
                       LocalDate endAt,
                       String color) {
        Optional.ofNullable(title).ifPresent(updateTitle -> this.title = updateTitle);
        Optional.ofNullable(smallCategory).ifPresent(category -> this.smallCategory = category);
        Optional.ofNullable(startAt).ifPresent(startDay -> {
            if (!this.state.equals(State.BEFORE.toString())) {
                throw new MogakException(ErrorCode.CAN_MODIFIED_BEFORE_START_MOGAK);
            }
            this.state = State.registerState(startDay, endAt, LocalDate.now()).toString();
            this.startAt = startDay;
        });
        Optional.ofNullable(endAt).ifPresent(endDay -> this.endAt = endDay);
        Optional.ofNullable(color).ifPresent(updateColor -> this.color = updateColor);
    }
}

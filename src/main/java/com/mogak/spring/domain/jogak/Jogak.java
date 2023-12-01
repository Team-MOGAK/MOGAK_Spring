package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@Table(name = "jogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Jogak extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_id")
    private Mogak mogak;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_category")
    private MogakCategory category;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "jogak")
    private List<JogakPeriod> jogakPeriods = new ArrayList<>();
    @Column(nullable = false)
    private Boolean isRoutine;
    @Column(name = "number_achievements")
    private Integer numberAchievements;
    @Column(name = "start_at")
    private LocalDate startAt;
    @Column(name = "end_at")
    private LocalDate endAt;
    private String state;

    public List<String> getPeriods() {
        return this.getJogakPeriods()
                .stream()
                .map(JogakPeriod::getPeriod)
                .map(Period::getDays)
                .collect(Collectors.toList());
    }

    public void update(String title, Boolean isRoutine, LocalDate endAt) {
        Optional.ofNullable(title).ifPresent(updateTitle -> this.title = updateTitle);
        Optional.ofNullable(isRoutine).ifPresent(routine -> this.isRoutine = routine);
        Optional.ofNullable(endAt).ifPresent(endDate -> this.endAt = endDate);
    }

    public void updateCategory(MogakCategory category) {
        this.category = category;
    }

//    public void start(LocalDateTime now) {
//        if (this.startTime != null) {
//            throw new JogakException(ErrorCode.ALREADY_START_JOGAK);
//        }
//        if (!this.getCreatedAt().toLocalDate().equals(LocalDate.now())) {
//            throw new JogakException(ErrorCode.WRONG_START_MIDNIGHT_JOGAK);
//        }
//
//        this.startTime = now;
//        this.state = JogakState.ONGOING.name();
//    }

//    public void end(LocalDateTime now) {
//       if (this.startTime == null ||
//               !this.state.equals(JogakState.ONGOING.name())) {
//           throw new JogakException(ErrorCode.NOT_START_JOGAK);
//       }
//       if (this.endTime != null) {
//           throw new JogakException(ErrorCode.ALREADY_END_JOGAK);
//       }
//
//       LocalDateTime startOfDay = getCreatedAt().toLocalDate().atStartOfDay();
//       LocalDateTime deadLine = getCreatedAt().toLocalDate().atStartOfDay().plusDays(1).plusHours(4);
//       if (now.isBefore(startOfDay) || now.isAfter(deadLine)) {
//           throw new JogakException(ErrorCode.OVERDUE_DEADLINE_JOGAK);
//       }
//
//       this.endTime = now;
//       this.state = JogakState.SUCCESS.name();
//    }

    public void updateState(JogakState state) {
        this.state = state.toString();
    }

}

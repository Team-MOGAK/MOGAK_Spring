package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.global.SoftDeletableEntity;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Table(name = "daily_jogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyJogak extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_jogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_id")
    private Mogak mogak;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogak_id")
    private Jogak jogak;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_category")
    private MogakCategory category;
    @Column(nullable = false)
    private String title;
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DailyJogakStatus status;
    @Column(nullable = false)
    private Boolean isRoutine;

    public boolean isSuccess() {
        return status == DailyJogakStatus.SUCCESS;
    }

    public boolean isFail() {
        return status == DailyJogakStatus.FAIL;
    }

    public Boolean getIsAchievement() {
        return isSuccess();
    }

    public void updateStatus(DailyJogakStatus status) {
        this.status = status;
    }

    public void updateJogak(Jogak jogak) {
        this.jogak = jogak;
        this.title = jogak.getTitle();
        this.isRoutine = jogak.getIsRoutine();
    }

    public static JogakResponseDto.GetRoutineJogakDto getRoutineJogakDto(DailyJogak dailyJogak) {
        return JogakResponseDto.GetRoutineJogakDto.of(
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                dailyJogak.isSuccess(),
                dailyJogak.getTitle()
        );
    }

    public static JogakResponseDto.GetRoutineJogakDto getFutureRoutineJogakDto(LocalDate date, String title) {
        return JogakResponseDto.GetRoutineJogakDto.of(-1L, date, false, title);
    }
}

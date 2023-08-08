package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.mogak.Mogak;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Table(name = "jogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Jogak extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogak_id")
    private Mogak mogak;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;

    public void start(LocalDateTime now) {
        if (this.startTime != null) {
            throw new RuntimeException("이미 시작한 조각입니다.");
        }
        if (!this.getCreatedAt().toLocalDate().equals(LocalDate.now())) {
            throw new RuntimeException("자정을 넘긴 조각입니다");
        }

        this.startTime = now;
        this.state = JogakState.ONGOING.name();
    }

    public void end(LocalDateTime now) {
       if (this.startTime == null ||
               !this.state.equals(JogakState.ONGOING.name())) {
           throw new RuntimeException("시작하지 않은 조각입니다");
       }
       if (this.endTime != null) {
           throw new RuntimeException("이미 종료한 조각입니다");
       }

       LocalDateTime startOfDay = getCreatedAt().toLocalDate().atStartOfDay();
       LocalDateTime deadLine = getCreatedAt().toLocalDate().atStartOfDay().plusDays(1).plusHours(4);
       if (now.isBefore(startOfDay) || now.isAfter(deadLine)) {
           throw new RuntimeException("기한을 넘긴 조각입니다");
       }

       this.endTime = now;
       this.state = JogakState.SUCCESS.name();
    }

    public void updateState(JogakState state) {
        this.state = state.toString();
    }

}

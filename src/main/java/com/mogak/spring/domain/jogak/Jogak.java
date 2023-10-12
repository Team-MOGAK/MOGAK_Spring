package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
    private String state;

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

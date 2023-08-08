package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.mogak.Mogak;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public void start(LocalDateTime now) {
        if (this.startTime != null) {
            throw new RuntimeException("이미 시작한 조각입니다.");
        }
        if (!Objects.equals(this.getCreatedAt().toLocalDate(), LocalDate.now())) {
            throw new RuntimeException("자정을 넘긴 조각입니다");
        }

        this.startTime = now;
    }
}

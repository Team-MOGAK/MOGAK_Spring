package com.mogak.spring.domain.mogak;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "mogak_period")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakPeriod {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mogak_period_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="period_id")
    private Period period;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mogak_id")
    private Mogak mogak;

    public static MogakPeriod of(Period period, Mogak mogak) {
        return MogakPeriod.builder()
                .period(period)
                .mogak(mogak)
                .build();
    }

    public void updatePeriod(Period period) {
        this.period = period;
    }
}

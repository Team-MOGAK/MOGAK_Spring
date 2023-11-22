package com.mogak.spring.domain.jogak;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "jogak_period")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JogakPeriod {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jogak_period_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id")
    private Period period;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogak_id")
    private Jogak jogak;

    public void updatePeriod(Period period) {
        this.period = period;
    }
}

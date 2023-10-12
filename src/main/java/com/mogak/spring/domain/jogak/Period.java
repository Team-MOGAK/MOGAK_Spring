package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.jogak.JogakPeriod;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "period")
@Entity
public class Period {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private int id;
    @Column(nullable = false)
    private String days;
    @Builder.Default
    @OneToMany(mappedBy = "period")
    private List<JogakPeriod> jogakPeriods = new ArrayList<>();
}

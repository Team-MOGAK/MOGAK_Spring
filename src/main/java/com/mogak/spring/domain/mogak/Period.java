package com.mogak.spring.domain.mogak;

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
    @OneToMany(mappedBy = "period")
    private List<MogakPeriod> mogakPeriods = new ArrayList<>();
}

package com.mogak.spring.domain.jogak;

import com.mogak.spring.domain.base.BaseEntity;
import com.mogak.spring.domain.mogak.Mogak;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

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
    @Column(nullable = false)
    private LocalTime startTime;
    private LocalTime endTime;
}

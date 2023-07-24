package com.mogak.spring.domain.mogak;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "mogak_category")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mogak_category_id")
    private Long id;
    @Column(nullable = false)
    private String name;

}

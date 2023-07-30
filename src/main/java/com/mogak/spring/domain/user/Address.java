package com.mogak.spring.domain.user;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "address")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int id;
    private String name;
}

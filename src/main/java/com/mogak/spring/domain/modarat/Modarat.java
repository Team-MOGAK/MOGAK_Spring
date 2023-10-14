package com.mogak.spring.domain.modarat;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Table(name = "modarat")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Modarat extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String validation;

    public static Modarat of(User user, ModaratRequestDto.CreateModaratDto request) {
        return Modarat.builder()
                .user(user)
                .color(request.getColor())
                .validation(Validation.ACTIVE.toString())
                .build();
    }

}

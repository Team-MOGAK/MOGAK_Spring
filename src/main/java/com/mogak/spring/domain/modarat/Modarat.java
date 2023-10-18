package com.mogak.spring.domain.modarat;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "mogak")
    private List<Mogak> mogakList = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String validation;

    public void update(ModaratRequestDto.UpdateModaratDto request) {
        this.title = request.getTitle();
        this.color = request.getColor();
    }

}

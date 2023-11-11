package com.mogak.spring.domain.modarat;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.BaseEntity;
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
    @Column(name = "modarat_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
//    @OneToMany(mappedBy = "mogak")
//    private List<Mogak> mogaks = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String validation;

    public void update(String title, String color) {
        this.title = title;
        this.color = color;
    }

}

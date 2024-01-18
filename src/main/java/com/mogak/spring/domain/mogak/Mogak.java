package com.mogak.spring.domain.mogak;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Table(name = "mogak")
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogak extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mogak_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modarat_id")
    private Modarat modarat;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "big_category")
    private MogakCategory bigCategory;
    @Column(name = "small_category")
    private String smallCategory;
    @Builder.Default
    @OneToMany(mappedBy = "mogak")
    private List<Jogak> jogaks = new ArrayList<>();
    @Column(nullable = false)
    private String title;
    private String color;
    @Column(nullable = false)
    private String validation;

    public void updateBigCategory(MogakCategory category) {
        this.bigCategory = category;
    }

    public void update(String title,
                       String smallCategory,
                       String color) {
        Optional.ofNullable(title).ifPresent(updateTitle -> this.title = updateTitle);
        Optional.ofNullable(smallCategory).ifPresent(category -> this.smallCategory = category);
        Optional.ofNullable(color).ifPresent(updateColor -> this.color = updateColor);
    }
}

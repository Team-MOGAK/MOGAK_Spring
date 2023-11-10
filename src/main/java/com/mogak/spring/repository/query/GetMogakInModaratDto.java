package com.mogak.spring.repository.query;

import com.mogak.spring.domain.mogak.MogakCategory;
import lombok.*;

@Builder
@Getter
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMogakInModaratDto {
    private String title;
    private MogakCategory bigCategory;
    private String smallCategory;
    private String color;

    public GetMogakInModaratDto(String title, MogakCategory bigCategory, String smallCategory, String color) {
        this.title = title;
        this.bigCategory = bigCategory;
        this.smallCategory = smallCategory;
        this.color = color;
    }
}

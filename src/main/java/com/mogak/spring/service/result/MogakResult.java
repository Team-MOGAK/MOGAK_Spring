package com.mogak.spring.service.result;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;

public record MogakResult(Long id, String title, MogakCategory bigCategory, String smallCategory, String color) {
    public static MogakResult from(Mogak mogak) {
        return new MogakResult(
                mogak.getId(),
                mogak.getTitle(),
                mogak.getBigCategory(),
                mogak.getSmallCategory(),
                mogak.getColor()
        );
    }
}

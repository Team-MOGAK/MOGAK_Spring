package com.mogak.spring.web.dto.mogakdto;

import com.mogak.spring.domain.mogak.MogakCategory;

public record MogakResponse(Long id, String title, MogakCategory bigCategory, String smallCategory, String color) {
}

package com.mogak.spring.web.dto.mogakdto;

import com.mogak.spring.domain.mogak.MogakCategory;
import java.util.List;

public class MogakResponseDto {
    public record GetMogakListDto(List<GetMogakDto> mogaks, int size) {
    }

    public record GetMogakDto(Long id, String title, MogakCategory bigCategory, String smallCategory, String color) {
    }
}

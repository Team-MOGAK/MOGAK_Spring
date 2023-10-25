package com.mogak.spring.web.dto.ModaratDto;

import com.mogak.spring.repository.query.GetMogakInModaratDto;
import com.mogak.spring.web.dto.MogakResponseDto;
import lombok.*;

import java.util.List;

public class ModaratResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateModaratDto {
        private Long id;
        private String title;
        private String color;
    }

}

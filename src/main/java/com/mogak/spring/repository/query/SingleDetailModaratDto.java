package com.mogak.spring.repository.query;

import java.util.List;

public record SingleDetailModaratDto(
        Long id,
        String title,
        String color,
        List<GetMogakInModaratDto> mogakDtoList
) {
    public SingleDetailModaratDto(Long id, String title, String color) {
        this(id, title, color, List.of());
    }

    public SingleDetailModaratDto withMogakDtoList(List<GetMogakInModaratDto> mogakDtoList) {
        return new SingleDetailModaratDto(id, title, color, mogakDtoList);
    }
}

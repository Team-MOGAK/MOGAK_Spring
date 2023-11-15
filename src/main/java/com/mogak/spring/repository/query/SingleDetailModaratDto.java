package com.mogak.spring.repository.query;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingleDetailModaratDto {
    private Long id;
    private String title;
    private String color;
    private List<GetMogakInModaratDto> mogakDtoList;

    public SingleDetailModaratDto(Long id, String title, String color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public void updateMogakList(List<GetMogakInModaratDto> mogakDtoList) {
        this.mogakDtoList = mogakDtoList;
    }
}
package com.mogak.spring.web.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class JogakRequestDto {
    @Getter
    public static class CreateJogakDto {
        private Long mogakId;

        public CreateJogakDto(Long mogakId) {
            this.mogakId = mogakId;
        }
    }

}

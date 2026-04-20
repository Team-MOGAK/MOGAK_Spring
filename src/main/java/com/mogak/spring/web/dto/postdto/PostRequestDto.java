package com.mogak.spring.web.dto.postdto;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PostRequestDto {

    @Getter
    public static class CreatePostDto{
        @NotNull
        private LocalDate targetDate;
        private String contents;
    }



    @Getter
    public static class UpdatePostDto{
        public String contents;
    }


}

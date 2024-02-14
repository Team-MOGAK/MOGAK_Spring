package com.mogak.spring.web.dto.jogakdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class JogakRequestDto {
    @Getter
    @Schema(description = "조각 생성 DTO")
    public static class CreateJogakDto {
        @NotNull
        private Long mogakId;
        @Schema(description = "최소 한글자 이상 100자 이하로 입력 가능합니다")
        @NotNull @Size(min = 1, max = 100)
        private String title;
        @NotNull
        @Schema(description = "Boolean 형으로, True or False 값을 입력해주시면 됩니다")
        private Boolean isRoutine;
        @Schema(description = "isRoutine과 연관된 필드로, isRoutine 값이 True이면 여기에 MONDAY, TUESDAY 등의 날짜를 입력하시면 됩니다. " +
                "isRoutine 값이 False 값인 경우에 이 필드에 값을 입력하면 안됩니다")
        private List<String> days;
        @Schema(description = "오늘의 조각을 생성하기 위해 오늘 날짜를 입력하시면 됩니다 format: YYYY-MM-DD", example = "2024-02-14")
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate today;
        @Schema(description = "조각의 종료 날짜를 입력하시면 됩니다(선택). format: YYYY-MM-DD", example = "2024-02-14")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

    @Getter
    public static class UpdateJogakDto {
        @Size(min = 1, max = 100)
        private String title;
        @NotNull
        private Boolean isRoutine;
        @Schema(description = "isRoutine과 연관된 필드로, isRoutine 값이 True이면 여기에 MONDAY, TUESDAY 등의 날짜를 입력하시면 됩니다. " +
                "isRoutine 값이 False 값인 경우에 이 필드에 값을 입력하면 안됩니다", example = "{MONDAY, TUESDAY, SUNDAY}")
        private List<String> days;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

}

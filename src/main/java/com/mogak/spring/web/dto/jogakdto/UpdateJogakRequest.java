package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record UpdateJogakRequest(
        @Size(min = 1, max = 100) String title,
        @JsonProperty("isRoutine") Boolean isRoutine,
        @Schema(description = "isRoutine과 연관된 필드로, isRoutine 값이 True이면 여기에 MONDAY, TUESDAY 등의 날짜를 입력하시면 됩니다. " +
                "isRoutine 값이 False 값인 경우에 이 필드에 값을 입력하면 안됩니다", example = "{MONDAY, TUESDAY, SUNDAY}")
        List<String> days,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
}

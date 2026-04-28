package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.DailyJogakStatus;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JogakConverter {
    public static Jogak toInitialJogak(Mogak mogak, String title, Boolean isRoutine, LocalDate today, LocalDate endAt) {
        return Jogak.builder()
                .user(mogak.getUser())
                .mogak(mogak)
                .category(mogak.getBigCategory())
                .title(title)
                .isRoutine(isRoutine)
                .achievements(0)
                .startAt(today)
                .endAt(endAt)
                .build();
    }

    public static Jogak toJogak(DailyJogak dailyJogak) {
        return Jogak.builder()
                .mogak(dailyJogak.getMogak())
                .category(dailyJogak.getCategory())
                .title(dailyJogak.getTitle())
                .isRoutine(dailyJogak.getIsRoutine())
                .build();
    }

    public static DailyJogak toDailyJogakResponseDto(Jogak jogak) {
        return DailyJogak.builder()
                .id(-1L)
                .mogak(jogak.getMogak())
                .jogak(jogak)
                .title(jogak.getTitle())
                .category(jogak.getCategory())
                .isRoutine(jogak.getIsRoutine())
                .targetDate(LocalDate.now())
                .status(DailyJogakStatus.PENDING)
                .build();
    }

    public static DailyJogak toInitialDailyJogak(Jogak jogak, LocalDate targetDate) {
        return DailyJogak.builder()
                .mogak(jogak.getMogak())
                .category(jogak.getCategory())
                .title(jogak.getTitle())
                .targetDate(targetDate)
                .status(DailyJogakStatus.PENDING)
                .jogak(jogak)
                .isRoutine(jogak.getIsRoutine())
                .build();
    }

    public static DailyJogak toInitialDailyJogak(Jogak jogak) {
        return toInitialDailyJogak(jogak, LocalDate.now());
    }

    public static JogakResponseDto.CreateJogakDto toCreateJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.CreateJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                null,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.CreateJogakDto toCreateJogakResponseDto(Jogak jogak, List<String> days) {
        return JogakResponseDto.CreateJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.DetailJogakDto toGetJogakDetailResponseDto(Jogak jogak, String color) {
        return JogakResponseDto.DetailJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                null,
                color,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.DetailJogakDto toGetJogakDetailResponseDto(Jogak jogak, String color, List<String> days) {
        return JogakResponseDto.DetailJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                color,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.GetOneTimeJogakDto toGetOneTimeJogakResponseDto(Jogak jogak, Boolean bool) {
        return JogakResponseDto.GetOneTimeJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                bool,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.GetJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                null,
                null,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak, Boolean isAlreadyAdded) {
        return JogakResponseDto.GetJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                jogak.getPeriods(),
                isAlreadyAdded,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static JogakResponseDto.GetDailyJogakDto toGetDailyJogakResponseDto(DailyJogak dailyJogak) {
        Long jogakId = dailyJogak.getJogak() == null ? null : dailyJogak.getJogak().getId();
        return JogakResponseDto.GetDailyJogakDto.of(
                jogakId,
                dailyJogak.getId(),
                dailyJogak.getMogak().getTitle(),
                dailyJogak.getCategory().getName(),
                dailyJogak.getTitle(),
                dailyJogak.getIsRoutine(),
                dailyJogak.isSuccess()
        );
    }

    public static JogakResponseDto.GetDailyJogakDto toGetFutureDailyJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.GetDailyJogakDto.of(
                jogak.getId(),
                -1L,
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                false
        );
    }

    public static JogakResponseDto.GetOneTimeJogakListDto toGetOneTimeJogakListResponseDto(List<Jogak> jogaks, List<DailyJogak> dailyJogaks) {
        List<JogakResponseDto.GetOneTimeJogakDto> responseJogaks = jogaks.stream()
                .map(jogak -> toGetOneTimeJogakResponseDto(jogak, findCorrespondingDailyJogak(jogak, dailyJogaks)))
                .collect(Collectors.toList());
        return new JogakResponseDto.GetOneTimeJogakListDto(jogaks.size(), responseJogaks);
    }

    private static Boolean findCorrespondingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> Objects.equals(dailyJogak.getJogak(), jogak));
    }

    public static JogakResponseDto.GetDailyJogakListDto toGetDailyJogakListResponseDto(List<DailyJogak> dailyJogaks) {
        List<JogakResponseDto.GetDailyJogakDto> responseDailyJogaks = dailyJogaks.stream()
                .map(JogakConverter::toGetDailyJogakResponseDto)
                .collect(Collectors.toList());
        return new JogakResponseDto.GetDailyJogakListDto(dailyJogaks.size(), responseDailyJogaks);
    }

    public static JogakResponseDto.JogakSuccessDto toSuccessJogak(Jogak jogak) {
        return new JogakResponseDto.JogakSuccessDto(
                jogak.getMogak().getTitle(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName()
        );
    }

    public static JogakResponseDto.JogakDailyJogakDto toJogakDailyJogakDto(Jogak jogak, DailyJogak dailyJogak) {
        return JogakResponseDto.JogakDailyJogakDto.of(
                jogak.getId(),
                dailyJogak.getId(),
                dailyJogak.getTitle(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getIsRoutine(),
                null,
                dailyJogak.isSuccess(),
                jogak.getAchievements()
        );
    }
}

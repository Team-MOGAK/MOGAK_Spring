package com.mogak.spring.support;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.Role;
import com.mogak.spring.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TestFixtureFactory {

    private TestFixtureFactory() {
    }

    public static Address address(String name) {
        return Address.builder()
                .name(name)
                .build();
    }

    public static Job job(String name) {
        return Job.builder()
                .name(name)
                .build();
    }

    public static User user(Long id, String email, String nickname, Job job, Address address) {
        return User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .job(job)
                .address(address)
                .validation(Validation.ACTIVE.name())
                .role(Role.USER)
                .build();
    }

    public static Modarat modarat(Long id, User user, String title, String color) {
        return Modarat.builder()
                .id(id)
                .user(user)
                .title(title)
                .color(color)
                .validation(Validation.ACTIVE.name())
                .build();
    }

    public static MogakCategory category(Integer id, String name) {
        MogakCategory.MogakCategoryBuilder builder = MogakCategory.builder()
                .name(name);
        if (id != null) {
            builder.id(id);
        }
        return builder.build();
    }

    public static Mogak mogak(Long id, User user, Modarat modarat, MogakCategory category, String title, String color) {
        return Mogak.builder()
                .id(id)
                .user(user)
                .modarat(modarat)
                .bigCategory(category)
                .smallCategory("소분류")
                .title(title)
                .color(color)
                .validation(Validation.ACTIVE.name())
                .jogaks(new ArrayList<>())
                .build();
    }

    public static Jogak jogak(Long id, Mogak mogak, String title, boolean isRoutine, LocalDate startAt, LocalDate endAt, int achievements) {
        return Jogak.builder()
                .id(id)
                .mogak(mogak)
                .user(mogak.getUser())
                .category(mogak.getBigCategory())
                .title(title)
                .isRoutine(isRoutine)
                .achievements(achievements)
                .startAt(startAt)
                .endAt(endAt)
                .dailyJogaks(new ArrayList<>())
                .jogakPeriods(new ArrayList<>())
                .build();
    }

    public static Period period(int id, String day) {
        return Period.builder()
                .id(id)
                .days(day)
                .build();
    }

    public static Period period(String day) {
        return Period.builder()
                .days(day)
                .build();
    }

    public static JogakPeriod jogakPeriod(Jogak jogak, Period period) {
        return JogakPeriod.builder()
                .jogak(jogak)
                .period(period)
                .build();
    }

    public static DailyJogak dailyJogak(Long id, Jogak jogak, boolean isAchievement) {
        return DailyJogak.builder()
                .id(id)
                .mogak(jogak.getMogak())
                .jogak(jogak)
                .category(jogak.getCategory())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .isAchievement(isAchievement)
                .build();
    }

    public static void setCreatedAt(Object entity, LocalDateTime createdAt) {
        ReflectionTestUtils.setField(entity, "createdAt", createdAt);
    }

    public static void attachJogaks(Mogak mogak, List<Jogak> jogaks) {
        ReflectionTestUtils.setField(mogak, "jogaks", jogaks);
    }

    public static void attachJogakPeriods(Jogak jogak, List<JogakPeriod> jogakPeriods) {
        ReflectionTestUtils.setField(jogak, "jogakPeriods", jogakPeriods);
    }
}

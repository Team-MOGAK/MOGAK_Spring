package com.mogak.spring.signature;

import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceInterfaceSignatureTest {

    @Test
    @DisplayName("UserService 인터페이스의 public 메서드 시그니처는 변경되지 않는다")
    void userServiceSignature() {
        assertThat(toSignatures(UserService.class)).containsExactlyInAnyOrder(
                "create(com.mogak.spring.web.dto.userdto.UserRequestDto$CreateUserDto,com.mogak.spring.web.dto.userdto.UserRequestDto$UploadImageDto):com.mogak.spring.web.dto.userdto.UserResponseDto$CreateDto",
                "verifyNickname(java.lang.String):java.lang.Boolean",
                "getToken(com.mogak.spring.domain.user.User):java.lang.String",
                "updateNickname(com.mogak.spring.web.dto.userdto.UserRequestDto$UpdateNicknameDto):void",
                "getProfileImgName():java.lang.String",
                "updateJob(com.mogak.spring.web.dto.userdto.UserRequestDto$UpdateJobDto):void",
                "getUserByEmail(java.lang.String):com.mogak.spring.domain.user.User",
                "updateImg(com.mogak.spring.web.dto.userdto.UserRequestDto$UpdateImageDto):void",
                "getUserProfile():com.mogak.spring.web.dto.userdto.UserResponseDto$GetUserDto"
        );
    }

    @Test
    @DisplayName("MogakService 인터페이스의 public 메서드 시그니처는 변경되지 않는다")
    void mogakServiceSignature() {
        assertThat(toSignatures(MogakService.class)).containsExactlyInAnyOrder(
                "create(com.mogak.spring.web.dto.mogakdto.MogakRequestDto$CreateDto):com.mogak.spring.web.dto.mogakdto.MogakResponseDto$GetMogakDto",
                "updateMogak(com.mogak.spring.web.dto.mogakdto.MogakRequestDto$UpdateDto):com.mogak.spring.web.dto.mogakdto.MogakResponseDto$GetMogakDto",
                "getMogakDtoList(java.lang.Long):com.mogak.spring.web.dto.mogakdto.MogakResponseDto$GetMogakListDto",
                "deleteMogak(java.lang.Long):void",
                "getJogaks(java.lang.Long,java.time.LocalDate):java.util.List<com.mogak.spring.web.dto.jogakdto.JogakResponseDto$GetJogakDto>"
        );
    }

    @Test
    @DisplayName("JogakService 인터페이스의 public 메서드 시그니처는 변경되지 않는다")
    void jogakServiceSignature() {
        assertThat(toSignatures(JogakService.class)).containsExactlyInAnyOrder(
                "createRoutineJogakToday():void",
                "createJogak(com.mogak.spring.web.dto.jogakdto.JogakRequestDto$CreateJogakDto):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$CreateJogakDto",
                "updateJogak(java.lang.Long,com.mogak.spring.web.dto.jogakdto.JogakRequestDto$UpdateJogakDto):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$CreateJogakDto",
                "getDailyJogaks(java.time.LocalDate):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$GetOneTimeJogakListDto",
                "getDayJogaks(java.time.LocalDate):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$GetDailyJogakListDto",
                "startJogak(java.lang.Long):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$JogakDailyJogakDto",
                "successJogak(java.lang.Long):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$JogakDailyJogakDto",
                "deleteJogak(java.lang.Long):void",
                "getRoutineJogaks(java.time.LocalDate,java.time.LocalDate):java.util.List<com.mogak.spring.web.dto.jogakdto.JogakResponseDto$GetRoutineJogakDto>",
                "failJogak(java.lang.Long):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$JogakDailyJogakDto",
                "getJogakDetail(java.lang.Long):com.mogak.spring.web.dto.jogakdto.JogakResponseDto$DetailJogakDto"
        );
    }

    private Set<String> toSignatures(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> !method.isSynthetic() && !method.isBridge())
                .map(this::toSignature)
                .collect(Collectors.toSet());
    }

    private String toSignature(Method method) {
        String parameters = Arrays.stream(method.getGenericParameterTypes())
                .map(Type::getTypeName)
                .collect(Collectors.joining(","));
        return method.getName() + "(" + parameters + "):" + method.getGenericReturnType().getTypeName();
    }
}

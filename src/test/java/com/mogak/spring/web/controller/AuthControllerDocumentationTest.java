package com.mogak.spring.web.controller;

import com.mogak.spring.web.dto.authdto.SocialLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerDocumentationTest {

    @Test
    @DisplayName("소셜 로그인 Swagger 문서는 provider별 토큰 타입과 주요 실패 응답을 설명한다")
    void socialLoginSwaggerDocumentsProviderContract() throws Exception {
        Method method = AuthController.class.getMethod("loginSocial", String.class, SocialLoginRequest.class);

        Operation operation = method.getAnnotation(Operation.class);
        Parameter providerParameter = method.getParameters()[0].getAnnotation(Parameter.class);

        assertThat(operation).isNotNull();
        assertThat(operation.description())
                .contains("apple/google")
                .contains("id token")
                .contains("kakao")
                .contains("access token")
                .contains("기존 이메일 계정");
        assertThat(operation.responses())
                .extracting(ApiResponse::responseCode)
                .contains("200", "400", "409");
        assertThat(providerParameter.schema().allowableValues())
                .containsExactly("apple", "google", "kakao");
    }
}

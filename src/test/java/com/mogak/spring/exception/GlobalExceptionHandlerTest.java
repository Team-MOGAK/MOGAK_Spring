package com.mogak.spring.exception;

import com.mogak.spring.global.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    @DisplayName("BaseException은 매핑된 에러 코드 상태코드와 함께 반환한다")
    void shouldReturnMappedErrorCodeForBaseException() throws Exception {
        mockMvc.perform(get("/global-exceptions/base"))
                .andExpect(status().is(NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(ErrorCode.NOT_EXIST_USER.getStatus().name()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_EXIST_USER.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_EXIST_USER.getMessage()));
    }

    @Test
    @DisplayName("NullPointerException은 BAD_REQUEST로 매핑된다")
    void shouldReturnBadRequestForNullPointerException() throws Exception {
        mockMvc.perform(get("/global-exceptions/null-pointer"))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(ErrorCode.BAD_REQUEST.getStatus().name()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.getCode()));
    }

    @Test
    @DisplayName("잘못된 JSON 바디는 HttpMessageNotReadableException으로 BAD_REQUEST를 반환한다")
    void shouldReturnBadRequestForMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/global-exceptions/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":}"))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(ErrorCode.BAD_REQUEST.getStatus().name()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.getCode()));
    }

    @Test
    @DisplayName("RuntimeException은 INTERNAL_SERVER_ERROR로 매핑된다")
    void shouldReturnInternalServerErrorForUnexpectedRuntimeException() throws Exception {
        mockMvc.perform(get("/global-exceptions/runtime"))
                .andExpect(status().is(INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.status").value(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().name()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
    }

    @RestController
    static class ExceptionTestController {

        @GetMapping("/global-exceptions/base")
        public void throwBaseException() {
            throw new BaseException(ErrorCode.NOT_EXIST_USER);
        }

        @GetMapping("/global-exceptions/null-pointer")
        public void throwNullPointerException() {
            throw new NullPointerException("npe");
        }

        @GetMapping("/global-exceptions/runtime")
        public void throwRuntimeException() {
            throw new IllegalStateException("unexpected");
        }

        @PostMapping("/global-exceptions/request")
        public void receiveRequest(@RequestBody TestRequest request) {
            // Intentionally left blank
        }
    }

    record TestRequest(String name) {
    }
}

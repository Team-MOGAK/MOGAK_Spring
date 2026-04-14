package com.mogak.spring.jwt;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import feign.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * request를 intercept해 jwt 검증
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (preflight(request)) {
            return true;
        }
        //헤더에서 토큰 받아옴
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if (accessToken == null) {
            log.info("token이 존재하지 않습니다");
            throw new BaseException(ErrorCode.EMPTY_TOKEN);
        }
        log.info("현재 accesstoken : " + accessToken);
        //토큰 확인되면  유저 정보 받아오고 authectication 객체 저장
        if (jwtTokenProvider.validateAccessToken(accessToken)) {//access token 검증
            setAuthentication(accessToken);
            log.info("인증 성공");
        }
        return true;
    }

    /**
     * http method인지 확인
     */
    public boolean preflight(HttpServletRequest request) {
        return request.getMethod() == Request.HttpMethod.OPTIONS.name();
    }

    public void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

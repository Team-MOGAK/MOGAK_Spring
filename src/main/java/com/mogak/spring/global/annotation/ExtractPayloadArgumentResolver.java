package com.mogak.spring.global.annotation;

import com.mogak.spring.exception.CommonException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.global.JwtArgumentResolver;
import com.mogak.spring.login.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class ExtractPayloadArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String jwtToken = JwtArgumentResolver.extractToken(request)
                .orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        jwtTokenProvider.getUserPk(jwtToken);
        jwtToken = jwtToken.substring("Bearer ".length());
        return Long.valueOf(jwtTokenProvider.getUserPk(jwtToken));
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return false;
    }

}

package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import feign.Request;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
//    private static final List<String> EXCLUDE_URLS= Arrays.asList("/swagger-ui/index.html","/api/auth/login","/api/auth/refresh","/api/auth/logout","/api/users/nickname/verify","/api/users/join");

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            System.out.println(accessToken);
            jwtTokenProvider.parseToken(accessToken);
            if(isLogout(accessToken)){
                throw new AuthException(ErrorCode.LOGOUT_TOKEN);
            }
            setAuthentication(accessToken); //securitycontextholder에 토큰 등록
        } catch (ExpiredJwtException e){//만료기간 체크
            throw new AuthException(ErrorCode.EXPIRE_TOKEN);
        } catch (SignatureException | UnsupportedJwtException e){ //기존서명확인불가&jwt 구조 문제
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String accessToken){
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 해당 토큰이 로그아웃된 토큰인지 체크
     */
    public boolean isLogout(String accessToken){
        if(redisService.getValues(accessToken) == "logout"){
            return true;
        }
        return false;
    }

//    private boolean shouldExclude(HttpServletRequest request){
//        return EXCLUDE_URLS.stream()
//                .anyMatch(url -> {
//                    System.out.println(request.getRequestURI() + " / " + url);
//                    return request.getRequestURI().contains(url);
//                });
//    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {"/","/swagger-ui/**", "/v3/api-docs", "/swagger-resources/**",
                "/webjars/**", "/swagger-ui.html", "/swagger-ui/index.html","/api-docs/**",
                "/api/auth/login","/api/auth/refresh","/api/auth/logout",
                "/api/auth/withdraw", "/api/users/nickname/verify","/api/users/join"};
        String path=request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }
}

package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;
//    private static final List<String> EXCLUDE_URLS= Arrays.asList("/swagger-ui/index.html","/api/auth/login","/api/auth/refresh","/api/auth/logout","/api/users/nickname/verify","/api/users/join");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("현재 request" + request);
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        log.info("현재 accesstoken: " + accessToken);
        System.out.print("현재 accesstoken: " + accessToken);

        try {
            log.info("현재 accesstoken: " + accessToken);
            if(accessToken==null){
                throw new AuthException(ErrorCode.EMPTY_TOKEN);
            }
            if(isLogout(accessToken)){ //로그아웃 검증
                throw new AuthException(ErrorCode.LOGOUT_TOKEN);
            }
            if(jwtTokenProvider.validateAccessToken(accessToken)){//access token 검증
                setAuthentication(accessToken); //검증된 토큰만 securitycontextholder에 토큰 등록
                System.out.print("인증성공");
            }
        } catch (ExpiredJwtException e){//만료기간 체크
            log.info("만료된 토큰입니다");
            throw new AuthException(ErrorCode.EXPIRE_TOKEN);
        } catch (SignatureException | UnsupportedJwtException | AuthException e){ //기존서명확인불가&jwt 구조 문제
            log.info("잘못된 토큰입니다");
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Authentication 객체 생성
     */
    public void setAuthentication(String accessToken){
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 해당 토큰이 로그아웃된 토큰인지 체크
     */
    public boolean isLogout(String accessToken){
        String values = redisService.getValues(accessToken);
        if(values != null){
            return "logout".equals(values);
        }
        return false;
    }

    /**
     * 해당 uri는 filter x
     */

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

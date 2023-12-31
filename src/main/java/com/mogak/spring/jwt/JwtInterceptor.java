package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import feign.Request;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * request를 intercept해 jwt 검증
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor{

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(preflight(request)){
            return true;
        }
        try{
            //헤더에서 토큰 받아옴
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            log.info("현재 accesstoken : " + accessToken);
            //로그아웃 여부 확인 추가
            if(isLogout(accessToken)){
                throw new IllegalStateException("Invalid Token");
            }
            //토큰 확인되면  유저 정보 받아오고 authectication 객체 저장
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
        return true;
    }
    /**
     * http method인지 확인
     */
    public boolean preflight(HttpServletRequest request){
        return request.getMethod() == Request.HttpMethod.OPTIONS.name();
    }

    public void setAuthentication(String accessToken){
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean isLogout(String accessToken){
        String values = redisService.getValues(accessToken);
        if(values != null){
            return "logout".equals(values);
        }
        return false;
    }
}

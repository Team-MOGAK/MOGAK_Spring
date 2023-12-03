package com.mogak.spring.jwt;

import com.mogak.spring.redis.RedisService;
import feign.Request;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
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
            jwtTokenProvider.parseToken(accessToken);
            //로그아웃 여부 확인 추가
            if(isLogout(accessToken)){
                throw new IllegalStateException("Invalid Token");
            }
            //토큰 확인되면  유저 정보 받아오고 authectication 객체 저장
            setAuthentication(accessToken);
        }catch (ExpiredJwtException e){
            throw new IllegalStateException("Expired token");
        }catch (NullPointerException e){
            throw new IllegalStateException("Null");
        }catch (Exception e){
            throw new IllegalStateException("Invalid token");
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
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        if(redisService.getValues(userEmail).isEmpty()){
            return false;
        }
        return true;
    }
}

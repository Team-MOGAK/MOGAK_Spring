package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import feign.Request;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.secret}")
    private String secretKey;
//    private static final List<String> EXCLUDE_URLS= Arrays.asList("/swagger-ui/index.html","/api/auth/login","/api/auth/refresh","/api/auth/logout","/api/users/nickname/verify","/api/users/join");

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String accessToken = resolveAccessToken(request);
        log.info("현재 accesstoken: " + accessToken);

        try {
            log.info("현재 accesstoken: " + accessToken);
//            if(accessToken==null){
//                throw new AuthException(ErrorCode.EMPTY_TOKEN);
//            }
//            if(isLogout(accessToken)){ //로그아웃 검증
//                throw new AuthException(ErrorCode.LOGOUT_TOKEN);
//            }
//            if(jwtTokenProvider.validateAccessToken(accessToken)){//access token 검증
//                setAuthentication(accessToken); //검증된 토큰만 securitycontextholder에 토큰 등록
//                log.info("인증 성공");
//            }
            setAuthentication(accessToken);
        } catch (ExpiredJwtException e){//만료기간 체크
            throw new AuthException(ErrorCode.EXPIRE_TOKEN);
        } catch (SignatureException | UnsupportedJwtException | AuthException e){ //기존서명확인불가&jwt 구조 문제
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
        filterChain.doFilter(request, response);
    }

    //header에서 access token 가져오기
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("Authorization")!=null & request.getHeader("Authorization").startsWith("Bearer ")){
            return request.getHeader("Authorization").substring(7);
        }
        return null;
    }

    //user email 검색
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("email").toString();
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
        if(userDetails == null){
            throw new UsernameNotFoundException("User not found for useremail: " + getUserPk(token));
        }
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }


    /**
     * Authentication 객체 생성
     */
    public void setAuthentication(String accessToken){
//        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        Authentication authentication = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 해당 토큰이 로그아웃된 토큰인지 체크
     */
    public boolean isLogout(String accessToken){
        if(redisService.getValues(accessToken).equals("logout")){
            return true;
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
        log.info("필터 처리에 제외된 uri 입니다");
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }
}

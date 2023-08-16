package com.mogak.spring.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        RequestModifyParameter req = new RequestModifyParameter(request);
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring("Bearer ".length());
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String userPk = jwtTokenProvider.getUserPk(jwtToken);
                RequestModifyParameter fixedReq = new RequestModifyParameter(request);
                fixedReq.setParameter("userId", userPk);
                req = fixedReq;
            }
        }
        filterChain.doFilter(req, response);
    }

}

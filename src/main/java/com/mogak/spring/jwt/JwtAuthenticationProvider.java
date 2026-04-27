package com.mogak.spring.jwt;

import com.mogak.spring.global.BaseException;
import com.mogak.spring.global.ErrorCode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String ROLE_CLAIM = "role";
    private static final String EMAIL_CLAIM = "email";
    private static final String ID_CLAIM = "id";

    private final JwtTokenCodec jwtTokenCodec;

    public JwtAuthenticationProvider(JwtTokenCodec jwtTokenCodec) {
        this.jwtTokenCodec = jwtTokenCodec;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }

        String token = jwtAuthenticationToken.getToken();
        if (token == null || token.isBlank()) {
            throw new JwtAuthenticationException(ErrorCode.EMPTY_TOKEN, "Missing access token");
        }

        Jwt jwt;
        try {
            jwt = jwtTokenCodec.decode(token);
        } catch (BaseException exception) {
            throw new JwtAuthenticationException(resolveTokenErrorCode(exception), exception);
        }

        String tokenType = jwt.getClaimAsString(TOKEN_TYPE_CLAIM);
        if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
            throw new JwtAuthenticationException(ErrorCode.WRONG_TOKEN, "Invalid access token type");
        }

        String email = normalize(jwt.getClaimAsString(EMAIL_CLAIM));
        String subject = normalize(jwt.getSubject());
        String role = normalize(jwt.getClaimAsString(ROLE_CLAIM));
        Long userId = extractUserId(jwt.getClaim(ID_CLAIM));

        if (subject == null || role == null || userId == null) {
            throw new JwtAuthenticationException(ErrorCode.WRONG_TOKEN, "Required access token claims are missing");
        }
        if (!String.valueOf(userId).equals(subject)) {
            throw new JwtAuthenticationException(ErrorCode.WRONG_TOKEN, "Token subject does not match id claim");
        }

        AuthenticatedUser principal = new AuthenticatedUser(userId, email, role);
        return new JwtAuthenticationToken(
                principal,
                token,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Long extractUserId(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue) {
            String trimmed = stringValue.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(trimmed);
            } catch (NumberFormatException exception) {
                throw new JwtAuthenticationException(ErrorCode.WRONG_TOKEN, exception);
            }
        }
        return null;
    }

    private ErrorCode resolveTokenErrorCode(BaseException exception) {
        if (ErrorCode.EXPIRE_TOKEN.getCode().equals(exception.getCode())) {
            return ErrorCode.EXPIRE_TOKEN;
        }
        return ErrorCode.WRONG_TOKEN;
    }
}

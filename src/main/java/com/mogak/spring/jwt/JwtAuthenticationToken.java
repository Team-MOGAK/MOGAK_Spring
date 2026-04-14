package com.mogak.spring.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public final class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final AuthenticatedUser principal;

    public JwtAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
        this.principal = null;
        super.setAuthenticated(false);
    }

    public JwtAuthenticationToken(AuthenticatedUser principal, String token,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal != null ? principal : token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getName() {
        return principal == null ? "" : principal.getUsername();
    }
}

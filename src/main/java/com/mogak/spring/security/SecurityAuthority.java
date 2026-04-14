package com.mogak.spring.security;

import com.mogak.spring.domain.user.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityAuthority {
    USER(Role.USER.getKey()),
    ADMIN(Role.ADMIN.getKey()),
    PENDING("ROLE_PENDING");

    private final String authority;
}

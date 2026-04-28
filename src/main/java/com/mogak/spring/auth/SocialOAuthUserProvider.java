package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;

public interface SocialOAuthUserProvider {
    boolean supports(SocialProvider provider);

    SocialUserProfile getUser(String token);
}

package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;

public record SocialUserProfile(SocialProvider provider, String providerUserId, String email, Boolean emailVerified) {
}

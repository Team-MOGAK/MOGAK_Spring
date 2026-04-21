package com.mogak.spring.jwt;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("사용자 인증 정보 조회는 활성 사용자 이메일 조회를 사용한다")
    void loadUserByUsernameUsesActiveUserLookup() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        when(userRepository.findActiveByEmail("user@test.com")).thenReturn(Optional.of(user));

        var userDetails = customUserDetailsService.loadUserByUsername("user@test.com");

        assertThat(userDetails.getUsername()).isEqualTo("user@test.com");
        verify(userRepository).findActiveByEmail("user@test.com");
    }
}

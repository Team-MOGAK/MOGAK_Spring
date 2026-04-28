package com.mogak.spring.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled("External Apple API call is excluded from the core test safety net")
@SpringBootTest
public class AppleClientTest {

    @Autowired
    private AppleClient appleClient;

    @Test
    @DisplayName("Apple public keys 요청 후 응답을 받는다")
    void getPublicKeys() {
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        List<ApplePublicKey> keys = applePublicKeys.keys();
        boolean isRequestedKeysNonNull = keys.stream()
                .allMatch(this::isAllNotNull);
        assertThat(isRequestedKeysNonNull).isTrue();
    }

    private boolean isAllNotNull(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.kty()) && Objects.nonNull(applePublicKey.kid()) &&
                Objects.nonNull(applePublicKey.use()) && Objects.nonNull(applePublicKey.alg()) &&
                Objects.nonNull(applePublicKey.n()) && Objects.nonNull(applePublicKey.e());
    }
}

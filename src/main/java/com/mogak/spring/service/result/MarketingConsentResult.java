package com.mogak.spring.service.result;

public record MarketingConsentResult(
        boolean marketingAgreed,
        boolean advertisementAgreed
) {
}

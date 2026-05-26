package com.mogak.spring.web.dto.consentdto;

import com.mogak.spring.service.result.MarketingConsentResult;

public record MarketingConsentResponse(
        boolean marketingAgreed,
        boolean advertisementAgreed
) {
    public static MarketingConsentResponse from(MarketingConsentResult result) {
        return new MarketingConsentResponse(result.marketingAgreed(), result.advertisementAgreed());
    }
}

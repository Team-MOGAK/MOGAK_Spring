package com.mogak.spring.web.dto.consentdto;

import com.mogak.spring.service.command.MarketingConsentCommand;
import jakarta.validation.constraints.AssertTrue;

public record MarketingConsentPatchRequest(
        Boolean marketingAgreed,
        Boolean advertisementAgreed
) {
    @AssertTrue(message = "변경할 동의 값을 입력해주세요.")
    public boolean hasAnyAgreement() {
        return marketingAgreed != null || advertisementAgreed != null;
    }

    public MarketingConsentCommand toCommand() {
        return new MarketingConsentCommand(marketingAgreed, advertisementAgreed);
    }
}

package com.mogak.spring.service.command;

public record MarketingConsentCommand(
        Boolean marketingAgreed,
        Boolean advertisementAgreed
) {
    public boolean isEmpty() {
        return marketingAgreed == null && advertisementAgreed == null;
    }
}

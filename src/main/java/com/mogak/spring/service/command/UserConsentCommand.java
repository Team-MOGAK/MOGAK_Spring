package com.mogak.spring.service.command;

public record UserConsentCommand(
        Long consentItemId,
        Boolean agreed
) {
}

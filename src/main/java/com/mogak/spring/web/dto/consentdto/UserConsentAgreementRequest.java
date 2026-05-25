package com.mogak.spring.web.dto.consentdto;

import com.mogak.spring.service.command.UserConsentCommand;
import jakarta.validation.constraints.NotNull;

public record UserConsentAgreementRequest(
        @NotNull
        Long consentItemId,
        @NotNull
        Boolean agreed
) {
    public UserConsentCommand toCommand() {
        return new UserConsentCommand(consentItemId, agreed);
    }
}

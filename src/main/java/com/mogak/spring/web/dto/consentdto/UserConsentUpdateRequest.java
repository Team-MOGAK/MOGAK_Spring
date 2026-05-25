package com.mogak.spring.web.dto.consentdto;

import com.mogak.spring.service.command.UserConsentCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserConsentUpdateRequest(
        @Valid
        List<@NotNull @Valid UserConsentAgreementRequest> consents
) {
    public List<UserConsentCommand> toCommands() {
        if (consents == null) {
            return List.of();
        }
        return consents.stream()
                .map(UserConsentAgreementRequest::toCommand)
                .toList();
    }
}

package com.mogak.spring.web.dto.userdto;

import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.web.dto.consentdto.UserConsentAgreementRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserCreateRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
        String nickname,
        @Size(min = 1, max = 100)
        String job,
        @Size(min = 1, max = 100)
        String address,
        @Valid
        List<@NotNull @Valid UserConsentAgreementRequest> consents
) {
    public UserCreateRequest(String nickname, String job, String address) {
        this(nickname, job, address, null);
    }

    public List<UserConsentCommand> toConsentCommands() {
        if (consents == null) {
            return List.of();
        }
        return consents.stream()
                .map(UserConsentAgreementRequest::toCommand)
                .toList();
    }
}

package com.mogak.spring.web.controller;

import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.ConsentService;
import com.mogak.spring.service.result.MarketingConsentResult;
import com.mogak.spring.web.dto.consentdto.ConsentItemResponse;
import com.mogak.spring.web.dto.consentdto.MarketingConsentPatchRequest;
import com.mogak.spring.web.dto.consentdto.MarketingConsentResponse;
import com.mogak.spring.web.dto.consentdto.UserConsentUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConsentController {

    private final ConsentService consentService;

    @GetMapping("/api/consents")
    public ResponseEntity<BaseResponse<List<ConsentItemResponse>>> getConsents() {
        List<ConsentItemResponse> responses = consentService.getActiveConsentItems().stream()
                .map(ConsentItemResponse::from)
                .toList();
        return ResponseEntity.ok(new BaseResponse<>(responses));
    }

    @GetMapping("/api/users/marketing-consent")
    public ResponseEntity<BaseResponse<MarketingConsentResponse>> getMarketingConsent(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        MarketingConsentResult result = consentService.getMarketingConsent(authenticatedUser.getUserId());
        return ResponseEntity.ok(new BaseResponse<>(MarketingConsentResponse.from(result)));
    }

    @PutMapping("/api/users/consents")
    public ResponseEntity<BaseResponse<ErrorCode>> updateUserConsents(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody UserConsentUpdateRequest request
    ) {
        consentService.updateUserConsents(authenticatedUser.getUserId(), request.toCommands());
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @PatchMapping("/api/users/marketing-consent")
    public ResponseEntity<BaseResponse<MarketingConsentResponse>> updateMarketingConsent(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody MarketingConsentPatchRequest request
    ) {
        MarketingConsentResult result = consentService.updateMarketingConsent(
                authenticatedUser.getUserId(),
                request.toCommand()
        );
        return ResponseEntity.ok(new BaseResponse<>(MarketingConsentResponse.from(result)));
    }
}

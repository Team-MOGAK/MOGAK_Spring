package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.command.MarketingConsentCommand;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import com.mogak.spring.service.result.MarketingConsentResult;
import java.util.List;

public interface ConsentService {
    List<ConsentItemResult> getActiveConsentItems();

    void saveUserConsents(User user, List<UserConsentCommand> consents);

    void updateUserConsents(Long userId, List<UserConsentCommand> consents);

    MarketingConsentResult getMarketingConsent(Long userId);

    MarketingConsentResult updateMarketingConsent(Long userId, MarketingConsentCommand command);
}

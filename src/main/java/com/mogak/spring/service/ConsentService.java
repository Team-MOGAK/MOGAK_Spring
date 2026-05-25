package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import java.util.List;

public interface ConsentService {
    List<ConsentItemResult> getActiveConsentItems();

    void saveUserConsents(User user, List<UserConsentCommand> consents);

    void updateUserConsents(Long userId, List<UserConsentCommand> consents);
}

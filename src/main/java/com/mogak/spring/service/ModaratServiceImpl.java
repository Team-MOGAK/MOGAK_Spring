package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.ModaratRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.repository.query.ModaratDetailProjection;
import com.mogak.spring.repository.query.MogakInModaratProjection;
import com.mogak.spring.service.result.ModaratDetailResult;
import com.mogak.spring.service.result.ModaratSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ModaratServiceImpl implements ModaratService {
    private final UserRepository userRepository;
    private final ModaratRepository modaratRepository;
    private final MogakRepository mogakRepository;
    private final MogakService mogakService;

    @Transactional
    @Override
    public Modarat create(Long userId, String title, String color) {
        User user = userRepository.findActiveById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return modaratRepository.save(Modarat.of(user, title, color));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long modaratId) {
        Modarat modarat = getOwnedModarat(modaratId, userId);
        List<Mogak> mogaks = mogakRepository.findAllByModaratId(modaratId);
        mogaks.forEach(mogak -> mogakService.deleteMogakCascadeAfterParentAuthorization(mogak.getId()));
        modarat.delete();
    }

    @Transactional
    @Override
    public Modarat update(Long userId, Long modaratId, String title, String color) {
        Modarat modarat = getOwnedModarat(modaratId, userId);
        modarat.update(title, color);
        return modarat;
    }

    @Override
    public ModaratDetailResult getDetailModarat(Long userId, Long modaratId) {
        Modarat modarat = getOwnedModarat(modaratId, userId);
        List<MogakInModaratProjection> mogaks = modaratRepository.findMogaksByModaratId(modarat.getId()).orElse(List.of());
        ModaratDetailProjection modaratProjection = modaratRepository.findOneDetailModarat(modaratId);
        return ModaratDetailResult.from(modaratProjection.withMogaks(mogaks));
    }

    @Override
    public List<ModaratSummaryResult> getModaratList(Long userId) {
        userRepository.findActiveById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return modaratRepository.findModaratsByUserId(userId).stream()
                .map(ModaratSummaryResult::from)
                .collect(Collectors.toList());
    }

    private Modarat getOwnedModarat(Long modaratId, Long userId) {
        Modarat modarat = modaratRepository.findActiveById(modaratId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_MODARAT));
        if (!Objects.equals(modarat.getUser().getId(), userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
        return modarat;
    }
}

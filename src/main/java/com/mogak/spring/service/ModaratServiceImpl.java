package com.mogak.spring.service;

import com.mogak.spring.converter.ModaratConverter;
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
import com.mogak.spring.repository.query.GetMogakInModaratDto;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;
import com.mogak.spring.web.dto.modaratdto.ModaratResponseDto;
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
    public Modarat create(Long userId, ModaratRequestDto.CreateModaratDto request) {
        User user = userRepository.findActiveById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return modaratRepository.save(ModaratConverter.toModarat(user, request));
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
    public Modarat update(Long userId, Long modaratId, ModaratRequestDto.UpdateModaratDto request) {
        Modarat modarat = getOwnedModarat(modaratId, userId);
        modarat.update(request.getTitle(), request.getColor());
        return modarat;
    }

    @Override
    public SingleDetailModaratDto getDetailModarat(Long userId, Long modaratId) {
        Modarat modarat = getOwnedModarat(modaratId, userId);
        List<GetMogakInModaratDto> mogakDtoList = modaratRepository.findMogakDtoListByModaratId(modarat.getId()).orElse(List.of());
        SingleDetailModaratDto modaratDto = modaratRepository.findOneDetailModarat(modaratId);
        modaratDto.updateMogakList(mogakDtoList);
        return modaratDto;
    }

    @Override
    public List<ModaratResponseDto.ModaratDto> getModaratList(Long userId) {
        userRepository.findActiveById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return modaratRepository.findModaratsByUserId(userId).stream()
                .map(ModaratConverter::toModaratDto)
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

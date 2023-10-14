package com.mogak.spring.service;

import com.mogak.spring.converter.ModaratConverter;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.ModaratRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ModaratServiceImpl implements ModaratService {
    private final UserRepository userRepository;
    private final ModaratRepository modaratRepository;

    @Transactional
    @Override
    public Modarat create(Long userId, ModaratRequestDto.CreateModaratDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return modaratRepository.save(ModaratConverter.toModarat(user, request));
    }

    @Transactional
    @Override
    public void delete(Long modaratId) {
        modaratRepository.deleteById(modaratId);
    }

    @Transactional
    @Override
    public Modarat update(Long modaratId, ModaratRequestDto.UpdateModaratDto request) {
        Modarat modarat = modaratRepository.findById(modaratId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_MODARAT));
        modarat.update(request);
        return modarat;
    }

}

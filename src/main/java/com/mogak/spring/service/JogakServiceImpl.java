package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.web.dto.JogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JogakServiceImpl implements JogakService {
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;

    @Override
    public Jogak createJogak(Long mogakId) {
        Mogak mogak = mogakRepository.findById(mogakId)
                .orElseThrow(IllegalArgumentException::new);
        if (!mogak.getState().equals(State.ONGOING.toString())) {
            throw new RuntimeException("진행중인 모각만 조각을 생성할 수 있습니다");
        }
        return jogakRepository.save(JogakConverter.toJogak(mogak));
    }


}

package com.mogak.spring.service;

import com.mogak.spring.service.result.mogak.GetJogakResult;
import com.mogak.spring.service.result.mogak.GetMogakListResult;
import com.mogak.spring.service.result.mogak.GetMogakResult;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface MogakService {
    GetMogakResult create(Long userId, MogakRequestDto.CreateDto createDto);
    GetMogakResult updateMogak(Long userId, MogakRequestDto.UpdateDto request);
    GetMogakListResult getMogakDtoList(Long userId, Long modaratId);
    void deleteMogak(Long userId, Long mogakId);
    void deleteMogakCascadeAfterParentAuthorization(Long mogakId);

    List<GetJogakResult> getJogaks(Long userId, Long mogakId, LocalDate day);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}

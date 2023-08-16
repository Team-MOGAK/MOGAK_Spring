package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.MogakRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

public interface MogakService {
    Mogak create(MogakRequestDto.CreateDto createDto, HttpServletRequest req);
    Mogak create(MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    Mogak updateMogak(MogakRequestDto.UpdateDto request);
    List<Mogak> getMogakList(HttpServletRequest req, int cursor, int size);
    void deleteMogak(Long mogakId);
    List<Mogak> getOngoingTodayMogakList(int name);
    void judgeMogakByDay(LocalDate day);
}

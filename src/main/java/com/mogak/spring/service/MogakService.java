package com.mogak.spring.service;

import com.mogak.spring.service.result.JogakSummaryResult;
import com.mogak.spring.service.result.MogakListResult;
import com.mogak.spring.service.result.MogakResult;

import java.time.LocalDate;
import java.util.List;

public interface MogakService {
    MogakResult create(Long userId, Long modaratId, String title, String bigCategory, String smallCategory, String color);
    MogakResult updateMogak(Long userId, Long mogakId, String title, String bigCategory, String smallCategory, String color);
    MogakListResult getMogakList(Long userId, Long modaratId);
    void deleteMogak(Long userId, Long mogakId);
    void deleteMogakCascadeAfterParentAuthorization(Long mogakId);

    List<JogakSummaryResult> getJogaks(Long userId, Long mogakId, LocalDate day);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}

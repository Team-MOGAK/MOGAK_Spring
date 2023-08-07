package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.web.dto.JogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class JogakServiceImpl implements JogakService {
    private final MogakService mogakService;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;

    /**
     * 자정에 Ongoing인 모든 모각 생성
     * TimeZone에 맞게 시간을 조정할 필요있음
     * */
    @Transactional
    @Scheduled(cron = "1 0 0 * * *")
    public void createJogakByScheduler() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayNum = dayOfWeek.getValue();

        List<Mogak> mogaks = mogakService.getOngoingTodayMogakList(dayNum);
        for (Mogak mogak: mogaks) {
            createJogak(mogak.getId());
        }
    }

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

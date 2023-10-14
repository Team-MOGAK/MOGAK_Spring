package com.mogak.spring.scheduler;

import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.MogakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Scheduler {

    @Autowired
    private JogakService jogakService;

    @Autowired
    private MogakService mogakService;

    @Scheduled(zone = "Asia/Seoul", cron = "1 0 0 * * *")
    public void createJogakByScheduler() {
        jogakService.createJogakToday();
    }

    @Scheduled(zone = "Asia/Seoul", cron = "0 1 0 * * *")
    public void failJogakMidnightByScheduler() {
        jogakService.failJogakAtMidnight();
    }

//    @Scheduled(zone = "Asia/Seoul", cron = "0 0 4 * * *")
//    public void failJogakAtFourByScheduler() {
//        jogakService.failJogakAtFour();
//    }

//    @Scheduled(zone = "Asia/Seoul", cron = "0 0 4 * * *")
//    public void judgeMogakAtFourByScheduler() {
//        mogakService.judgeMogakByDay(LocalDate.now());
//    }
}

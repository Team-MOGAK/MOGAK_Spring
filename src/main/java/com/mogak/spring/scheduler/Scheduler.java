package com.mogak.spring.scheduler;

import com.mogak.spring.service.JogakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private JogakService jogakService;

    @Scheduled(zone = "Asia/Seoul", cron = "1 0 0 * * *")
    public void createRoutineJogakByScheduler() {
        jogakService.createRoutineJogakToday();
    }

//    @Scheduled(zone = "Asia/Seoul", cron = "0 1 0 * * *")
//    public void failJogakMidnightByScheduler() {
//        jogakService.failRoutineJogakAtMidnight();
//    }

//    @Scheduled(zone = "Asia/Seoul", cron = "0 0 4 * * *")
//    public void failJogakAtFourByScheduler() {
//        jogakService.failJogakAtFour();
//    }

//    @Scheduled(zone = "Asia/Seoul", cron = "0 0 4 * * *")
//    public void judgeMogakAtFourByScheduler() {
//        mogakService.judgeMogakByDay(LocalDate.now());
//    }
}

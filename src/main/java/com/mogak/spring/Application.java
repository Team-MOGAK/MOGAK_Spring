package com.mogak.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class Application {

    /**
     * 서버 timezone 설정
     */
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	//추후 ec2에 올릴때는 주석처리. 현재 테스트환경에서 s3에 객체를 올리기 위한 것
	/*
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	*/

}

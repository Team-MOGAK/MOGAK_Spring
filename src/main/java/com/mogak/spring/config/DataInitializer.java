package com.mogak.spring.config;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.mogak.MogakPeriod;
import com.mogak.spring.domain.mogak.Period;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.*;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.MogakService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 기존의 데이터가 존재하는 경우 중복 입력이 안되게 처리 필요
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final MogakRepository mogakRepository;
    private final MogakCategoryRepository mogakCategoryRepository;
    private final MogakPeriodRepository mogakPeriodRepository;
    private final AddressRepository addressRepository;
    private final PeriodRepository periodRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JogakRepository jogakRepository;
    private final MogakService mogakService;
    private final JogakService jogakService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (periodRepository.findOneByDays("MONDAY").isEmpty()) {
            insertStaticData();
            insertDummyData();
        }
    }

    private void insertStaticData() {
        insertMogakCategory();
        insertAddress();
        insertPeriod();
        insertJob();
    }

    private void insertDummyData() {
        insertUser();
        insertMogak();
        insertJogak();
    }

    private void insertMogakCategory() {
        String[] taskArr = {
                "자격증", "대외활동", "운동", "인사이트", "공모전", "직무공부",
                "산업분석", "어학", "강연,강의", "프로젝트", "스터디", "기타"
        };
        for (String task: taskArr) {
            MogakCategory categoryEntity = DataInitializer.getCategory(task);
            mogakCategoryRepository.save(categoryEntity);
        }
    }

    protected static MogakCategory getCategory(String task) {
        return MogakCategory.builder()
                .name(task)
                .build();
    }

    private void insertAddress() {
        String[] addressArr = {
                "서울특별시", "세종특별자치시", "대전광역시", "광주광역시", "대구광역시",
                "부산광역시", "울산광역시", "경상남도", "경상북도", "전라남도", "전라북도",
                "충청남도", "충청북도", "강원도", "제주도", "독도/울릉도"
        };

        for (String address: addressArr) {
            Address addressEntity = getAddress(address);
            addressRepository.save(addressEntity);
        }
    }

    private static Address getAddress(String address) {
        return Address.builder()
                .name(address)
                .build();
    }

    private void insertPeriod() {
        String[] dayArr = {
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        };

        for (String day: dayArr) {
            Period periodEntity = getPeriod(day);
            periodRepository.save(periodEntity);
        }
    }

    private static Period getPeriod(String period) {
        return Period.builder()
                .days(period)
                .build();
    }

    private void insertJob() {
        String[] jobArr = {
                "기획/전략", "법무,사무,총무", "인사/HR", "회계/세무", "마케팅/광고/MD",
                "개발/데이터", "디자인", "물류/무역", "운전/운송/배송", "영업",
                "고객상담/TM", "금융/보험", "식/음료", "고객서비스/리테일", "엔지니어링/설계",
                "제조/생산", "교육", "건축/시설", "의료/바이오", "미디어/문화",
                "스포츠", "공공복지", "자영업", "군인", "의료",
                "회계사", "법무사", "노무사", "세무사", "관세사",
                "교사", "디지털노마드", "영상제작자", "크리에이터"
        };

        for (String job: jobArr) {
            Job jobEntity = getJob(job);
            jobRepository.save(jobEntity);
        }
    }

    private static Job getJob(String job) {
        return Job.builder()
                .name(job)
                .build();
    }

    //임시로 유저 5명 넣기
    private void insertUser() {
        String[] nickArr = {
                "hyun1234!", "hhhh9999!", "abcd78!@",
                "hello77@!", "mogak9!"
        };
        String[] jobArr = {
                "개발/데이터", "디자인", "물류/무역",
                "운전/운송/배송", "영업"
        };
        List<Job> jobList = new ArrayList<>();
        for (String job: jobArr) {
            Optional<Job> jobInDB = jobRepository.findJobByName(job);
            jobInDB.ifPresent(jobList::add);
        }

        String[] addressArr = {
                "서울특별시", "세종특별자치시", "대전광역시",
                "광주광역시", "대구광역시"
        };
        List<Address> addressList = new ArrayList<>();
        for (String address: addressArr) {
            Optional<Address> addressInDB = addressRepository.findAddressByName(address);
            addressInDB.ifPresent(addressList::add);
        }

        String[] emailArr = {
                "hyun123@naver.com", "gachon123@gc.ac.kr",
                "kyunghee123@khu.ac.kr", "kyonggi123@kgu.ac.kr",
                "kingwangjjang123@king.com"
        };

        int arrCnt = nickArr.length;
        for (int i = 0; i < arrCnt; i++) {
            User user = User.builder()
                    .nickname(nickArr[i])
                    .job(jobList.get(i))
                    .address(addressList.get(i))
                    .email(emailArr[i])
                    .validation(Validation.ACTIVE.toString())
                    .build();
            userRepository.save(user);
        }
    }

    private void insertMogak() {
        User user1 = userRepository.findById(1L).orElseThrow(RuntimeException::new);
        MogakCategory category1 = mogakCategoryRepository.findMogakCategoryByName("직무공부")
                .orElseThrow(RuntimeException::new);

        Mogak mogak1 = Mogak.builder()
                .user(user1)
                .title("스프링 해야딩")
                .category(category1)
                .state("ONGOING")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(7))
                .validation("VALID")
                .build();

        Mogak mogak2 = Mogak.builder()
                .user(user1)
                .title("스프링가링가링")
                .category(category1)
                .state("ONGOING")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(30))
                .validation("VALID")
                .build();

        Mogak mogak3 = Mogak.builder()
                .user(user1)
                .title("스프링딩동링딩동")
                .category(category1)
                .state("ONGOING")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(7))
                .validation("VALID")
                .build();

        Mogak mogak4 = Mogak.builder()
                .user(user1)
                .title("스프링딩동링딩동기기기기딩딩딩")
                .category(category1)
                .state("BEFORE")
                .startAt(LocalDate.now().plusDays(5))
                .endAt(LocalDate.now().plusDays(7))
                .validation("VALID")
                .build();

        mogakRepository.save(mogak1);
        mogakRepository.save(mogak2);
        mogakRepository.save(mogak3);
        mogakRepository.save(mogak4);

        Period monday = periodRepository.findOneByDays("MONDAY").orElseThrow(RuntimeException::new);
        Period tuesday = periodRepository.findOneByDays("TUESDAY").orElseThrow(RuntimeException::new);
        Period thursday = periodRepository.findOneByDays("THURSDAY").orElseThrow(RuntimeException::new);
        Period friday = periodRepository.findOneByDays("FRIDAY").orElseThrow(RuntimeException::new);

        saveMogakPeriod(mogak1, monday);
        saveMogakPeriod(mogak1, tuesday);
        saveMogakPeriod(mogak1, thursday);
        saveMogakPeriod(mogak1, friday);

        saveMogakPeriod(mogak2, thursday);
        saveMogakPeriod(mogak2, friday);

        saveMogakPeriod(mogak3, monday);
        saveMogakPeriod(mogak3, tuesday);
        saveMogakPeriod(mogak3, thursday);
        saveMogakPeriod(mogak3, friday);
    }

    private void saveMogakPeriod(Mogak mogak, Period day) {
        mogakPeriodRepository.save(MogakPeriod.builder()
                .mogak(mogak)
                .period(day)
                .build());
    }

    private void insertJogak() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayNum = dayOfWeek.getValue();

        List<Mogak> mogaks = mogakService.getOngoingTodayMogakList(dayNum);
        for (Mogak mogak: mogaks) {
            jogakService.createJogak(mogak.getId());
        }


    }

}

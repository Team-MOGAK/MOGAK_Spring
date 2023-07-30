package com.mogak.spring.config;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final MogakCategoryRepository mogakCategoryRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertMogakCategory();
    }

    private void insertMogakCategory() {
        MogakCategory certification = DataInitializer.getCategory("자격증");
        MogakCategory externalActivities = DataInitializer.getCategory("대외활동");
        MogakCategory exercise = DataInitializer.getCategory("운동");
        MogakCategory insight = DataInitializer.getCategory("인사이트");
        MogakCategory contest = DataInitializer.getCategory("공모전");
        MogakCategory jobStudy = DataInitializer.getCategory("직무공부");
        MogakCategory industryAnalysis = DataInitializer.getCategory("산업분석");
        MogakCategory language = DataInitializer.getCategory("어학");
        MogakCategory lectures = DataInitializer.getCategory("강연,강의");
        MogakCategory project = DataInitializer.getCategory("프로젝트");
        MogakCategory study = DataInitializer.getCategory("스터디");
        MogakCategory etc = DataInitializer.getCategory("기타");

        mogakCategoryRepository.save(certification);
        mogakCategoryRepository.save(externalActivities);
        mogakCategoryRepository.save(exercise);
        mogakCategoryRepository.save(insight);
        mogakCategoryRepository.save(contest);
        mogakCategoryRepository.save(jobStudy);
        mogakCategoryRepository.save(industryAnalysis);
        mogakCategoryRepository.save(language);
        mogakCategoryRepository.save(lectures);
        mogakCategoryRepository.save(project);
        mogakCategoryRepository.save(study);
        mogakCategoryRepository.save(etc);
    }

    protected static MogakCategory getCategory(String task) {
        return MogakCategory.builder()
                .name(task)
                .build();
    }

    private void insertAddress() {
        Address seoul = getAddress("서울특별시");
        Address sejong = getAddress("세종특별자치시");
        Address daejeon = getAddress("대전광역시");
        Address gwangju = getAddress("광주광역시");
        Address daegu = getAddress("대구광역시");
        Address busan = getAddress("부산광역시");
        Address ulsan = getAddress("울산광역시");
        Address gyeongnam = getAddress("경상남도");
        Address gyeongbuk = getAddress("경상북도");
        Address jeonnam = getAddress("전라남도");
        Address jeonbuk = getAddress("전라북도");
        Address chungnam = getAddress("충청남도");
        Address chungbuk = getAddress("충청북도");
        Address gangwon = getAddress("강원도");
        Address jeju = getAddress("제주도");
        Address dokdoAndUlleungdo = getAddress("독도/울릉도");

        addressRepository.save(seoul);
        addressRepository.save(sejong);
        addressRepository.save(daejeon);
        addressRepository.save(gwangju);
        addressRepository.save(daegu);
        addressRepository.save(busan);
        addressRepository.save(ulsan);
        addressRepository.save(gyeongnam);
        addressRepository.save(gyeongbuk);
        addressRepository.save(jeonnam);
        addressRepository.save(jeonbuk);
        addressRepository.save(chungnam);
        addressRepository.save(chungbuk);
        addressRepository.save(gangwon);
        addressRepository.save(jeju);
        addressRepository.save(dokdoAndUlleungdo);
    }

    private static Address getAddress(String address) {
        return Address.builder()
                .name(address)
                .build();
    }

}

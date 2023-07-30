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

import java.util.ArrayList;
import java.util.Arrays;
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
        insertAddress();
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

}

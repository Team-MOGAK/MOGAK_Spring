package com.mogak.spring.config;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final MogakCategoryRepository mogakCategoryRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    private void insertMogakCategory() {
        MogakCategory certification = DataInitializer.categoryConstructor("자격증");
        MogakCategory externalActivities = DataInitializer.categoryConstructor("대외활동");
        MogakCategory exercise = DataInitializer.categoryConstructor("운동");
        MogakCategory insight = DataInitializer.categoryConstructor("인사이트");
        MogakCategory contest = DataInitializer.categoryConstructor("공모전");
        MogakCategory jobStudy = DataInitializer.categoryConstructor("직무공부");
        MogakCategory industryAnalysis = DataInitializer.categoryConstructor("산업분석");
        MogakCategory language = DataInitializer.categoryConstructor("어학");
        MogakCategory lectures = DataInitializer.categoryConstructor("강연,강의");
        MogakCategory project = DataInitializer.categoryConstructor("프로젝트");
        MogakCategory study = DataInitializer.categoryConstructor("스터디");
        MogakCategory etc = DataInitializer.categoryConstructor("기타");

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

    protected static MogakCategory categoryConstructor(String task) {
        return MogakCategory.builder()
                .name(task)
                .build();
    }
    
}

package com.mogak.spring.service;

import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.service.result.metadata.MetadataOptionResult;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataServiceImplTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private MogakCategoryRepository mogakCategoryRepository;

    @InjectMocks
    private MetadataServiceImpl metadataService;

    @Test
    @DisplayName("메타데이터 서비스는 저장소 이름 목록을 ID 순서대로 반환한다")
    void returnsRepositoryNamesInOrder() {
        when(jobRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                TestFixtureFactory.job("개발/데이터"),
                TestFixtureFactory.job("기획/전략")
        ));
        when(addressRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                TestFixtureFactory.address("서울특별시"),
                TestFixtureFactory.address("경기도")
        ));
        when(mogakCategoryRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                TestFixtureFactory.category(1, "자격증"),
                TestFixtureFactory.category(2, "직무공부")
        ));

        assertThat(metadataService.getJobs()).containsExactly(
                new MetadataOptionResult("개발/데이터"),
                new MetadataOptionResult("기획/전략")
        );
        assertThat(metadataService.getAddresses()).containsExactly(
                new MetadataOptionResult("서울특별시"),
                new MetadataOptionResult("경기도")
        );
        assertThat(metadataService.getMogakCategories()).containsExactly(
                new MetadataOptionResult("자격증"),
                new MetadataOptionResult("직무공부")
        );
    }

    @Test
    @DisplayName("메타데이터 서비스는 고정 색상 팔레트를 반환한다")
    void returnsStaticColors() {
        assertThat(metadataService.getColors()).containsExactly(
                new MetadataOptionResult("#475FFD"),
                new MetadataOptionResult("#FF4C77"),
                new MetadataOptionResult("#F98A08"),
                new MetadataOptionResult("#11D796"),
                new MetadataOptionResult("#FF6827"),
                new MetadataOptionResult("#9C31FF"),
                new MetadataOptionResult("#21CAFF"),
                new MetadataOptionResult("#FF2F2F")
        );
    }
}

package com.mogak.spring.service;

import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.service.result.metadata.MetadataOptionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MetadataServiceImpl implements MetadataService {
    private static final List<String> COLORS = List.of(
            "#475FFD",
            "#FF4C77",
            "#F98A08",
            "#11D796",
            "#FF6827",
            "#9C31FF",
            "#21CAFF",
            "#FF2F2F"
    );

    private final JobRepository jobRepository;
    private final AddressRepository addressRepository;
    private final MogakCategoryRepository mogakCategoryRepository;

    @Override
    public List<MetadataOptionResult> getJobs() {
        return jobRepository.findAllByOrderByIdAsc().stream()
                .map(job -> new MetadataOptionResult(job.getName()))
                .toList();
    }

    @Override
    public List<MetadataOptionResult> getAddresses() {
        return addressRepository.findAllByOrderByIdAsc().stream()
                .map(address -> new MetadataOptionResult(address.getName()))
                .toList();
    }

    @Override
    public List<MetadataOptionResult> getMogakCategories() {
        return mogakCategoryRepository.findAllByOrderByIdAsc().stream()
                .map(mogakCategory -> new MetadataOptionResult(mogakCategory.getName()))
                .toList();
    }

    @Override
    public List<MetadataOptionResult> getColors() {
        return COLORS.stream()
                .map(MetadataOptionResult::new)
                .toList();
    }
}

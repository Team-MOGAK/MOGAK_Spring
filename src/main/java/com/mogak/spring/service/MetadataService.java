package com.mogak.spring.service;

import com.mogak.spring.service.result.metadata.MetadataOptionResult;

import java.util.List;

public interface MetadataService {
    List<MetadataOptionResult> getJobs();

    List<MetadataOptionResult> getAddresses();

    List<MetadataOptionResult> getMogakCategories();

    List<MetadataOptionResult> getColors();
}

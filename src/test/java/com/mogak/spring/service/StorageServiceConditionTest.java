package com.mogak.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

class StorageServiceConditionTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(StorageServiceConditionTest.TestConfig.class);

    @Test
    @DisplayName("feature.storage.enabled가 없거나 false면 DisabledStorageService가 StorageService로 등록된다")
    void wiresDisabledStorageServiceWhenFeatureIsMissingOrFalse() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StorageService.class);
            assertThat(context.getBean(StorageService.class)).isInstanceOf(DisabledStorageService.class);
            assertThat(context).hasSingleBean(DisabledStorageService.class);
            assertThat(context).doesNotHaveBean(AwsS3Service.class);
        });

        contextRunner
                .withPropertyValues("feature.storage.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(StorageService.class);
                    assertThat(context.getBean(StorageService.class)).isInstanceOf(DisabledStorageService.class);
                    assertThat(context).hasSingleBean(DisabledStorageService.class);
                    assertThat(context).doesNotHaveBean(AwsS3Service.class);
                });
    }

    @Test
    @DisplayName("feature.storage.enabled=true면 S3Client와 bucket 설정이 있을 때 AwsS3Service가 StorageService로 등록된다")
    void wiresAwsS3ServiceWhenFeatureEnabled() {
        contextRunner
                .withBean(S3Client.class, () -> Mockito.mock(S3Client.class))
                .withPropertyValues(
                        "feature.storage.enabled=true",
                        "spring.cloud.aws.s3.bucket=test-bucket"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(StorageService.class);
                    assertThat(context.getBean(StorageService.class)).isInstanceOf(AwsS3Service.class);
                    assertThat(context).hasSingleBean(AwsS3Service.class);
                    assertThat(context).doesNotHaveBean(DisabledStorageService.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    @Import({AwsS3Service.class, DisabledStorageService.class})
    static class TestConfig {
    }
}

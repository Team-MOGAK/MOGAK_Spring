package com.mogak.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(prefix = "feature.storage", name = "enabled", havingValue = "true")
public class AwsS3Config {

    @Bean
    public S3Client s3Client(
            @Value("${spring.cloud.aws.region.static}") String region,
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey
    ) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
}

package com.ph.core.story.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final SeaweedFsS3Properties props;

    @Bean
    public SdkHttpClient s3SdkHttpClient() {
        return ApacheHttpClient.builder()
                .connectionTimeout(Duration.ofMillis(props.getHttp().getConnectTimeoutMs()))
                .socketTimeout(Duration.ofMillis(props.getHttp().getReadTimeoutMs()))
                .connectionAcquisitionTimeout(
                        Duration.ofMillis(props.getHttp().getConnectionAcquireTimeoutMs()))
                .maxConnections(props.getHttp().getMaxConnections())
                .build();
    }

    @Bean
    public S3Client s3Client(SdkHttpClient s3SdkHttpClient) {
        return S3Client.builder()
                .httpClient(s3SdkHttpClient)
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .region(Region.of(props.getRegion()))
                .endpointOverride(URI.create(props.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        // SeaweedFS S3 is typically path-style.
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .region(Region.of(props.getRegion()))
                .endpointOverride(URI.create(props.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}

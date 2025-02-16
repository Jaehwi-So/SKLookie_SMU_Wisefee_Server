package com.sklookiesmu.wisefee.config.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
public class S3Config {

    @Value("${cloud.aws.s3.region}")
    private String region;

    @Value("${cloud.aws.auth}")
    private String authType;

    @Value("${cloud.aws.s3.profile:#{null}}")
    private String profile;

    @Bean
    public S3Client s3Client() {

        if(authType.equals("profile")){
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(ProfileCredentialsProvider.create(profile)) // CLI 프로파일 사용
                    .build();
        }
        else if(authType.equals("role")){
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(InstanceProfileCredentialsProvider.create()) // EC2 인스턴스 CredentialProvider 사용
                    .build();
        }
        return null;
    }
}
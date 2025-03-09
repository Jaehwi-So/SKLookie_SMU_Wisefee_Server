package com.sklookiesmu.wisefee.service.aws;

import com.sklookiesmu.wisefee.common.exception.common.FileDownloadException;
import com.sklookiesmu.wisefee.common.exception.common.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class S3PresignedService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String getUploadPresignedURL(String key) {
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                req -> req.signatureDuration(Duration.ofMinutes(15)) // 15분 유효기간
                        .putObjectRequest(
                                PutObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(key)
                                        .build()
                        )
        );
        return presignedRequest.url().toString();
    }

    public String getDownloadPresignedURL(String key) {
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                req -> req.signatureDuration(Duration.ofMinutes(15)) // 15분 유효기간
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(key)
                                        .build()
                        )
        );
        return presignedRequest.url().toString();
    }
}
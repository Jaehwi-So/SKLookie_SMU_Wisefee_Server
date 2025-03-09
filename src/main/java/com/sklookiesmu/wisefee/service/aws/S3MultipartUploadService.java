package com.sklookiesmu.wisefee.service.aws;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class S3MultipartUploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;



    /**
     * 1. Multipart Upload 초기화
     */
    public String initiateMultipartUpload(String key) {
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
        return response.uploadId();
    }

    /**
     * 2. Part별 Presigned URL 생성
     */
    public List<String> generatePresignedUrls(String key, String uploadId, int partCount) {
        List<String> presignedUrls = new ArrayList<>();

        for (int partNumber = 1; partNumber <= partCount; partNumber++) {

            final int finalPartNumber = partNumber;
            PresignedUploadPartRequest presignedRequest = s3Presigner.presignUploadPart(
                    req -> req.signatureDuration(Duration.ofMinutes(15)) // 15분 유효기간
                            .uploadPartRequest(
                                    UploadPartRequest.builder()
                                            .bucket(bucketName)
                                            .key(key)
                                            .uploadId(uploadId)  // 올바른 위치로 이동
                                            .partNumber(finalPartNumber)
                                            .build()
                            )
            );

            presignedUrls.add(presignedRequest.url().toString());
        }

        return presignedUrls;
    }

    /**
     * 3. 업로드 완료 요청
     */
    public void completeMultipartUpload(String key, String uploadId, List<Map<String, Object>> parts) {
        List<CompletedPart> completedParts = parts.stream()
                .map(part -> CompletedPart.builder()
                        .partNumber((Integer) part.get("partNumber"))
                        .eTag((String) part.get("eTag"))
                        .build())
                .collect(Collectors.toList());

        s3Client.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(CompletedMultipartUpload.builder()
                                .parts(completedParts)
                                .build())
                        .build()
        );
    }

    /**
     * 4. 업로드 실패 시 중단 요청
     */
    public void abortMultipartUpload(String key, String uploadId) {
        s3Client.abortMultipartUpload(
                AbortMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .build()
        );
    }
}
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

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String prefix, String filename) {
        try {
            String s3Key = prefix + "/" + filename;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType()) // MIME 타입 지정
                            .build(),
                    RequestBody.fromBytes(file.getBytes()) // MultipartFile -> byte[] 변환
            );
            return "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    public byte[] downloadFile(String prefix, String filename) {
        try {
            String s3Key = prefix + "/" + filename;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseBytes<?> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return responseBytes.asByteArray();
        } catch (S3Exception e) {
            throw new FileDownloadException("S3 파일 다운로드 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage());
        }
    }
}
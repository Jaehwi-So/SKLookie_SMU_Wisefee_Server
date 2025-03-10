package com.sklookiesmu.wisefee.api.v1.shared;

import com.sklookiesmu.wisefee.service.aws.S3MultipartUploadService;
import com.sklookiesmu.wisefee.service.aws.S3PresignedService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/multipart/s3")
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class FileMultipartUploadApiController {
    private final S3MultipartUploadService s3MultipartUploadService;

    @ApiOperation(
            value = "1. Multipart Upload 초기화 요청"
    )
    @GetMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiateMultipartUpload(@RequestParam String key) {
        String uploadId = s3MultipartUploadService.initiateMultipartUpload(key);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("uploadId", uploadId));
    }

    @ApiOperation(
            value = "2. 각 Part의 Presigned URL 생성 요청"
    )
    @GetMapping("/presigned-urls")
    public ResponseEntity<List<String>> getPresignedUploadUrls(@RequestParam String key,
                                                               @RequestParam String uploadId,
                                                               @RequestParam int partCount) {
        List<String> presignedUrls = s3MultipartUploadService.generatePresignedUrls(key, uploadId, partCount);
        return ResponseEntity.status(HttpStatus.OK).body(presignedUrls);
    }

    @ApiOperation(
            value = "3. 업로드 완료 요청"
    )
    @PostMapping("/complete")
    public ResponseEntity<String> completeMultipartUpload(@RequestParam String key,
                                                          @RequestParam String uploadId,
                                                          @RequestBody List<Map<String, Object>> parts) {
        s3MultipartUploadService.completeMultipartUpload(key, uploadId, parts);
        return ResponseEntity.status(HttpStatus.OK).body("전체 업로드 성공");
    }

    @ApiOperation(
            value = "4. 업로드 취소 처리"
    )
    @PostMapping("/abort")
    public ResponseEntity<String> abortMultipartUpload(@RequestParam String key,
                                                       @RequestParam String uploadId) {
        s3MultipartUploadService.abortMultipartUpload(key, uploadId);
        return ResponseEntity.status(HttpStatus.OK).body("업로드 취소");
    }



}
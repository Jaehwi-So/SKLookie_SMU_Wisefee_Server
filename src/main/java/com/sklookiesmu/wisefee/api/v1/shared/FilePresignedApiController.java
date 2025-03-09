package com.sklookiesmu.wisefee.api.v1.shared;

import com.sklookiesmu.wisefee.common.auth.SecurityUtil;
import com.sklookiesmu.wisefee.common.constant.AuthConstant;
import com.sklookiesmu.wisefee.dto.shared.file.FileInfoDto;
import com.sklookiesmu.wisefee.service.aws.S3PresignedService;
import com.sklookiesmu.wisefee.service.shared.interfaces.FileService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/file/s3")
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class FilePresignedApiController {
    private final S3PresignedService s3PresignedService;


    /**
     * S3 파일 업로드용 Presigned URL 발급
     */
    @ApiOperation(
            value = "파일 업로드 Presigned URL 발급"
    )
    @GetMapping("/presigned-upload")
    public ResponseEntity<String> getPresignedUploadUrl(@RequestParam String key) {
        String presignedUrl = s3PresignedService.getUploadPresignedURL(key);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrl);
    }

    /**
     * S3 파일 다운로드용 Presigned URL 발급
     */
    @ApiOperation(
            value = "파일 다운로드 Presigned URL 발급"
    )
    @GetMapping("/presigned-download")
    public ResponseEntity<String> getPresignedDownloadUrl(@RequestParam String key) {
        String presignedUrl = s3PresignedService.getDownloadPresignedURL(key);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrl);
    }



}
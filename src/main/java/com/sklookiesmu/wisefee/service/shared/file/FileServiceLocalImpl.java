package com.sklookiesmu.wisefee.service.shared.file;

import com.sklookiesmu.wisefee.common.constant.FileConstant;
import com.sklookiesmu.wisefee.common.exception.common.FileDownloadException;
import com.sklookiesmu.wisefee.common.exception.common.FileUploadException;
import com.sklookiesmu.wisefee.common.file.FileUtil;
import com.sklookiesmu.wisefee.domain.Member;
import com.sklookiesmu.wisefee.dto.shared.file.FileInfoDto;
import com.sklookiesmu.wisefee.repository.FileRepository;
import com.sklookiesmu.wisefee.service.shared.MemberServiceImpl;
import com.sklookiesmu.wisefee.service.shared.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "false")
public class FileServiceLocalImpl extends AbstractFileService {


    @Value("${upload.directory}")
    private String uploadDirectory;

    public FileServiceLocalImpl(FileRepository fileRepository, MemberServiceImpl memberService) {
        super(fileRepository, memberService);
    }


    /**
     * [경로를 기반으로 이미지 바이트 스트림 반환]
     * 해당 경로의 이미지의 바이트 스트림 형태를 얻음
     * @param [Path 파일 경로]
     * @return [byte[] 이미지 바이트 배열]
     */
    public byte[] getImageFile(FileInfoDto info){
        try {
            Path path = Paths.get(info.getFilePath());
            byte[] imageBytes = Files.readAllBytes(path);
            return imageBytes;
        } catch (IOException e) {
            throw new FileDownloadException("파일 다운로드 중 오류가 발생했습니다.");
        }
    }


    /**
     * [파일 업로드]
     * Multipart 파일을 입력받아 서버 내부 스토리지에 저장.
     * @param [MultipartFile 파일]
     * @return [FileinfoDto 파일 정보]
     */
    @Transactional()
    public FileInfoDto uploadFile(MultipartFile file){

        String originalFileName = file.getOriginalFilename();
        String mimeType = file.getContentType();


        //최대용량 체크
        if (file.getSize() > FileConstant.MAX_FILE_SIZE) {
            throw new FileUploadException("10MB 이하 파일만 업로드 할 수 있습니다.");
        }


        //MIMETYPE 체크
        if (!FileUtil.isImageFile(mimeType)) {
            throw new FileUploadException("이미지 파일만 업로드할 수 있습니다.");
        }

        //저장 파일명을 중복방지 고유명으로 변경
        String newFileName = generateUniqueFileName(originalFileName);
        Path filePath = Paths.get(uploadDirectory + File.separator + newFileName);


        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.");
        }
        return new FileInfoDto(file.getContentType(),
                newFileName,
                filePath.toString(),
                Long.toString(file.getSize()));

    }


}

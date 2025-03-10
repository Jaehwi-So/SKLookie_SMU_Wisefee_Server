package com.sklookiesmu.wisefee.service.shared.file;

import com.sklookiesmu.wisefee.common.constant.FileConstant;
import com.sklookiesmu.wisefee.common.exception.common.FileUploadException;
import com.sklookiesmu.wisefee.common.file.FileUtil;
import com.sklookiesmu.wisefee.domain.Member;
import com.sklookiesmu.wisefee.dto.shared.file.FileInfoDto;
import com.sklookiesmu.wisefee.repository.FileRepository;
import com.sklookiesmu.wisefee.service.aws.S3Service;
import com.sklookiesmu.wisefee.service.shared.MemberServiceImpl;
import com.sklookiesmu.wisefee.service.shared.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class AbstractFileService implements FileService {

    protected final FileRepository fileRepository;
    protected final MemberServiceImpl memberService;


    /**
     * [파일정보 저장]
     * 업로드된 파일 정보를 데이터베이스에 저장
     * @param [FileInfoDto 파일정보]
     * @return [Long 파일 PK]
     */
    @Transactional
    public Long addFileInfo(FileInfoDto fileinfo, Long memberPK){
        com.sklookiesmu.wisefee.domain.File file = new com.sklookiesmu.wisefee.domain.File();
        file.setFileType(fileinfo.getFileType()); //MIMETYPE(~확장자)
        file.setFileCapacity(fileinfo.getFileCapacity()); //용량
        file.setName(fileinfo.getName()); //이름
        file.setFilePath(fileinfo.getFilePath()); //경로
        file.setFileInfo(FileConstant.FILE_INFO_NO_USE); //정보
        file.setDeleted(false);

        Member member = memberService.getMember(memberPK);
        file.setMember(member);

        this.fileRepository.create(file);
        return file.getFileId();
    }

    /**
     * [해당 ID의 이미지 Info 얻어오기]
     * 업로드된 파일의 ID를 통해 경로 얻어오기
     * @param [Long 파일 PK]
     * @return [FileInfoDto 이미지 Info]
     */
    public FileInfoDto getImageInfoById(Long id){
        FileInfoDto info = this.fileRepository.getFilePathById(id);
        return info;
    }



    /**
     * [중복방지를 위한 파일 고유명 생성]
     * @param fileExtension 확장자
     * @return String 파일 고유이름
     */
    protected String generateUniqueFileName(String originalFileName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        // Random 객체 생성
        Random random = new Random();
        // 0 이상 100 미만의 랜덤한 정수 반환
        String randomNumber = Integer.toString(random.nextInt(Integer.MAX_VALUE));
        String timeStamp = dateFormat.format(new Date());
        return timeStamp + randomNumber + originalFileName;
    }

}

package com.devcareer.compass.application.resume.service

import com.devcareer.compass.application.resume.dto.ResumeResponse
import com.devcareer.compass.application.storage.FileStorageService
import com.devcareer.compass.domain.resume.Resume
import com.devcareer.compass.infrastructure.persistence.resume.ResumeJpaRepository
import com.devcareer.compass.presentation.exception.ResumeNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class ResumeService(
    private val resumeRepository: ResumeJpaRepository,
    private val fileStorageService: FileStorageService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun uploadResume(userId: Long, file: MultipartFile): ResumeResponse {
        logger.info("Upload resume for user: $userId, file: ${file.originalFilename}")

        //파일 저장
        val storedFile = fileStorageService.store(file)

        //엔티티 생성
        val resume = Resume(
            userId = userId,
            fileName = storedFile.fileName,
            originalFileName = storedFile.originalFileName,
            filePath = storedFile.filePath,
            fileSize = storedFile.fileSize,
            fileType = storedFile.fileType
        )

        //db 저장
        val savedResume = resumeRepository.save(resume)
        logger.info("Resume uploaded successfully: id=${savedResume.id}")

        //DTO 변환
        return ResumeResponse.from(savedResume)
    }

    fun getResume(id: Long):ResumeResponse{
        val resume = resumeRepository.findById(id).orElseThrow { ResumeNotFoundException(id) }
        return ResumeResponse.from(resume)
    }

    fun getUserResumes(userId: Long):List<ResumeResponse>{
        return resumeRepository.findByUserId(userId).map { ResumeResponse.from(it) }
    }

}
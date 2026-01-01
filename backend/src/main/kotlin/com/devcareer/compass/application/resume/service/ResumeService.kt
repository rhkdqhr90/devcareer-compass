package com.devcareer.compass.application.resume.service

import com.devcareer.compass.application.resume.dto.ResumeResponse
import com.devcareer.compass.application.storage.FileStorageService
import com.devcareer.compass.domain.resume.Resume
import com.devcareer.compass.infrastructure.ai.ResumeAnalysisService
import com.devcareer.compass.infrastructure.parsing.ResumeParsingService
import com.devcareer.compass.infrastructure.persistence.resume.ResumeJpaRepository
import com.devcareer.compass.presentation.exception.ResumeNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import tools.jackson.databind.ObjectMapper

@Service
@Transactional(readOnly = true)
class ResumeService(
    private val resumeRepository: ResumeJpaRepository,
    private val fileStorageService: FileStorageService,
    private val resumeParsingService: ResumeParsingService,
    private val objectMapper: ObjectMapper,
    private val resumeAnalysisService: ResumeAnalysisService,
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

        try{
            val parsedData = resumeParsingService.parseResume(storedFile.filePath, storedFile.fileType)
            val rawText = parsedData.rawText ?: throw Exception("텍스트 추출 실패")

            //ai 분석
            val aiAnalyzed = resumeAnalysisService.analyzeResume(parsedData.rawText)

            //파싱 결과 Json 으로 변환
            val parsedJson = objectMapper.writeValueAsString(parsedData)

            savedResume.markAsParsed(parsedJson)
            resumeRepository.save(savedResume)

            logger.info("Resume parsed successfully: id=${savedResume.id}")
        }catch (e: Exception){
            logger.error("Resume parsing failed: id=${savedResume.id}", e)
            savedResume.markAsFailed(e.message ?: "파싱 실패")
            resumeRepository.save(savedResume)
        }


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
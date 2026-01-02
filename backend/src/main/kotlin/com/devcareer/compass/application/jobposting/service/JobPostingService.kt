package com.devcareer.compass.application.jobposting.service

import com.devcareer.compass.application.jobposting.dto.ParsedJobData
import com.devcareer.compass.application.jobposting.dto.SkillStatistics
import com.devcareer.compass.application.storage.FileStorageService
import com.devcareer.compass.domain.jobposting.JobPosting
import com.devcareer.compass.domain.jobposting.JobPostingStatus
import com.devcareer.compass.infrastructure.parsing.JobPostingParsingService
import com.devcareer.compass.infrastructure.persistence.jobposting.JobPostingJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import tools.jackson.databind.ObjectMapper

@Service
@Transactional(readOnly = true)
class JobPostingService(
    private val jobPostingRepository: JobPostingJpaRepository,
    private val fileStorageService: FileStorageService,
    private val jobPostingParsingService: JobPostingParsingService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun uploadJobPostingPdf(file: MultipartFile): Long {
        logger.info("업로드 잡 포스팅 파일: ${file.originalFilename}")

        val storedFile = fileStorageService.store(file)

        val jobPosting = JobPosting(
            fileName = storedFile.fileName,
            originalFileName = storedFile.originalFileName,
            filePath = storedFile.filePath,
            fileSize = storedFile.fileSize
        )

        val saved = jobPostingRepository.save(jobPosting)

        try {
            val parsedData = jobPostingParsingService.parseJobPostingPdf(storedFile.filePath)
            val parsedJson = objectMapper.writeValueAsString(parsedData)

            saved.markAsParsed(parsedJson, parsedData.totalCount)
            jobPostingRepository.save(saved)

            logger.info("Job posting parsed: id=${saved.id}, count=${parsedData.totalCount}")
        } catch (e: Exception) {
            logger.error("Failed to parse job posting: id=${saved.id}", e)
            saved.markAsFailed(e.message ?: "파싱 실패")
            jobPostingRepository.save(saved)
        }

        return saved.id!!
    }

    fun getLatestSkillStatistics(): List<SkillStatistics> {
        val latestJobPost =
            jobPostingRepository.findTopByStatusOrderByCreatedAtAsc(JobPostingStatus.PARSED) ?: return emptyList()

        val parsedData = objectMapper.readValue(
            latestJobPost.parsedDataJson,
            ParsedJobData::class.java
        )

        val skillFrequency = mutableMapOf<String, Int>()

        parsedData.jobs.forEach { job ->
            (job.requiredSkills + job.preferredSkills).forEach { skill ->
                skillFrequency[skill] = (skillFrequency[skill] ?: 0) + 1
            }
        }

        val total = parsedData.totalCount.toDouble()

        return skillFrequency.map { (skill, count) ->
            SkillStatistics(
                skillName = skill,
                frequency = count,
                percentage = count / total * 100
            )
        }.sortedByDescending { it.percentage }
    }
}
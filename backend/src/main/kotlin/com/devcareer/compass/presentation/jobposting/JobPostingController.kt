package com.devcareer.compass.presentation.jobposting

import com.devcareer.compass.application.common.ApiResponse
import com.devcareer.compass.application.jobposting.dto.SkillStatistics
import com.devcareer.compass.application.jobposting.service.JobPostingService
import com.devcareer.compass.presentation.exception.FileRequiredException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/job-postings")
class JobPostingController(
    private val jobPostingService: JobPostingService
)
{
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/upload")
    fun uploadJobPostingPdf(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<Long>> {
        logger.info("Upload job posting pdf file: ${file.originalFilename}")

        if(file.isEmpty){
            throw FileRequiredException()
        }

        val jobPostingId = jobPostingService.uploadJobPostingPdf(file)
        return ResponseEntity.ok(ApiResponse.success(jobPostingId))
    }

    @GetMapping("/statistics/skills")
    fun getLatestSkillStatistics(): ResponseEntity<ApiResponse<List<SkillStatistics>>> {
        val skillStatistics = jobPostingService.getLatestSkillStatistics()
        return ResponseEntity.ok(ApiResponse.success(skillStatistics))
    }
}
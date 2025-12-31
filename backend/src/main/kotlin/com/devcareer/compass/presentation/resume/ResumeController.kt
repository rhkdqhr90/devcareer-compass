package com.devcareer.compass.presentation.resume

import com.devcareer.compass.application.common.ApiResponse
import com.devcareer.compass.application.resume.dto.ResumeResponse
import com.devcareer.compass.application.resume.service.ResumeService
import com.devcareer.compass.presentation.exception.FileRequiredException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/resumes")
class ResumeController(
    private val resumeService: ResumeService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/upload")
    fun uploadResume(@RequestParam("userId") userId: Long, @RequestParam("file") file: MultipartFile): ResponseEntity<ApiResponse<ResumeResponse>> {
        logger.info("Upload resume for user: $userId")

        if(file.isEmpty){
            throw FileRequiredException()
        }
        val response = resumeService.uploadResume(userId, file)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/{id}")
    fun getResume(@PathVariable id: Long): ResponseEntity<ApiResponse<ResumeResponse>>{
        val response = resumeService.getResume(id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/user/{userId}")
    fun getUserResume(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<ResumeResponse>>> {
        val response = resumeService.getUserResumes(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
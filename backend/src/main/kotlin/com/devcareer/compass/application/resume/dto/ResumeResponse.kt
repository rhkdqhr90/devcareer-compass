package com.devcareer.compass.application.resume.dto

import com.devcareer.compass.domain.resume.FileType
import com.devcareer.compass.domain.resume.Resume
import com.devcareer.compass.domain.resume.ResumeStatus
import java.time.LocalDateTime

data class ResumeResponse(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val fileSize: Long,
    val fileType: FileType,
    val status: ResumeStatus,
    val createdAt: LocalDateTime,
    val parsedAt: LocalDateTime?,
    val parsedDataJson: String? = null
){
    companion object{
        fun from(resume: Resume): ResumeResponse {
            return ResumeResponse(
                id = resume.id!!,
                fileName = resume.fileName,
                originalFileName = resume.originalFileName,
                fileSize = resume.fileSize,
                fileType = resume.fileType,
                status = resume.status,
                createdAt = resume.createdAt,
                parsedAt = resume.parsedAt,
                parsedDataJson = resume.parsedDataJson
            )
        }
    }
}

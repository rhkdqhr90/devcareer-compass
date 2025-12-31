package com.devcareer.compass.application.resume.dto

import jakarta.validation.constraints.NotNull


data class ResumeUploadRequest(
    @field:NotNull(message ="사용자 ID는 필수 입ㄴ디ㅏ.")
    val userId: Long?
)

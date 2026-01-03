package com.devcareer.compass.domain.jobposting

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name ="job_postings")
class JobPosting(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val fileName: String,
    val originalFileName: String,
    val filePath: String,
    val fileSize: Long,
    @Column(columnDefinition = "TEXT")
    var rawText: String? = null,

    @Column(columnDefinition = "TEXT")
    var parsedDataJson: String? = null,  // 추출된 공고들

    var totalCount: Int = 0,  // 총 공고 수

    @Enumerated(EnumType.STRING)
    var status: JobPostingStatus = JobPostingStatus.UPLOADED,
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var parsedAt: LocalDateTime? = null
) {
    fun markAsParsed(parsedJson: String, count: Int){
        this.parsedDataJson = parsedJson
        this.totalCount = count
        this.status = JobPostingStatus.PARSED
        this.parsedAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun markAsFailed(error: String){
        this.errorMessage = error
        this.status = JobPostingStatus.FAILED
        this.updatedAt = LocalDateTime.now()
    }
}
enum class JobPostingStatus {
    UPLOADED,   // 업로드됨
    PARSING,    // 파싱 중
    PARSED,     // 파싱 완료
    FAILED      // 실패
}
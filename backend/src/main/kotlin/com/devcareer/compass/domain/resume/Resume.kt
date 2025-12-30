package com.devcareer.compass.domain.resume

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name ="resumes")
class Resume(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 255)
    val fileName: String,

    @Column(nullable = false, length = 255)
    val originalFileName: String,

    @Column(nullable = false, length = 500)
    val filePath: String,

    @Column(nullable = false)
    val fileSize: Long,

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    val fileType: FileType,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: ResumeStatus = ResumeStatus.UPLOADED,

    @Column(columnDefinition = "TEXT")
    var parsedDataJson: String? = null,

    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var parsedAt: LocalDateTime? = null,)
    {
        fun markAsParsed(parsedJson: String){
            this.parsedDataJson = parsedJson
            this.status = ResumeStatus.PARSED
            this.parsedAt = LocalDateTime.now()
            this.updatedAt = LocalDateTime.now()
        }
        fun markAsFailed(error: String){
            this.errorMessage = error
            this.status = ResumeStatus.FAILED
            this.updatedAt = LocalDateTime.now()
        }
        fun isReadyForAnalysis(): Boolean{
            return status == ResumeStatus.PARSED && !parsedDataJson.isNullOrBlank()
        }
}
    enum class FileType{
        PDF,
        DOCX
    }
   enum class ResumeStatus{
        UPLOADED,
        PARSING,
        PARSED,
        FAILED
    }
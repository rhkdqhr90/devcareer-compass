package com.devcareer.compass.infrastructure.persistence.resume

import com.devcareer.compass.domain.resume.Resume
import com.devcareer.compass.domain.resume.ResumeStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResumeJpaRepository: JpaRepository<Resume, Long> {

    fun findByUserId(userId: Long): List<Resume>

    fun findByStatus(status: ResumeStatus): List<Resume>

    fun findByUserIdAndStatus(userId: Long, status: ResumeStatus): List<Resume>
}
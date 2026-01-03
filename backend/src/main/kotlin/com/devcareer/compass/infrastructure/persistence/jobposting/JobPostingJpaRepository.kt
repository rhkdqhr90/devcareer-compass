package com.devcareer.compass.infrastructure.persistence.jobposting

import com.devcareer.compass.domain.jobposting.JobPosting
import com.devcareer.compass.domain.jobposting.JobPostingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobPostingJpaRepository : JpaRepository<JobPosting, Long>{
    fun findByStatus(status: JobPostingStatus): List<JobPosting>
    fun findTopByStatusOrderByCreatedAtAsc(status: JobPostingStatus): JobPosting?
}
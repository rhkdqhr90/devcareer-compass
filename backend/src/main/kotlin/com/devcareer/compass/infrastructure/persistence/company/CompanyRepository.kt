package com.devcareer.compass.infrastructure.persistence.company

import com.devcareer.compass.domain.company.Company
import com.devcareer.compass.domain.company.CompanySize
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long>{
    fun findBySize(size: CompanySize): List<Company>
    fun findByNameContaining(name: String): List<Company>

}
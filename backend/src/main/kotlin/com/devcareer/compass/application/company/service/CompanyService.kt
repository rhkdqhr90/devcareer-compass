package com.devcareer.compass.application.company.service

import com.devcareer.compass.application.company.dto.CompanyRequest
import com.devcareer.compass.application.company.dto.CompanyResponse
import com.devcareer.compass.domain.company.Company
import com.devcareer.compass.infrastructure.persistence.company.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.log

@Service
@Transactional(readOnly = true)
class CompanyService(
    private val companyRepository: CompanyRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass);

    @Transactional
    fun createCompany(request: CompanyRequest): CompanyResponse {
        logger.info("Creating company: ${request.name}")

        val company = Company(
            name = request.name,
            description = request.description,
            size = request.size,
            requiredSkills = request.requiredSkills,
            preferredSkills = request.preferredSkills,
            location = request.location,
            websiteUrl = request.websiteUrl
        )

        val saved = companyRepository.save(company)
        logger.info("Company created successfully: id=${saved.id}")
        return toResponse(saved)
    }

    fun gerAllCompanies(): List<CompanyResponse> {
        return companyRepository.findAll().map { toResponse(it) }
    }
    fun getCompany(id: Long): CompanyResponse {
        val company = companyRepository.findById(id).orElseThrow { RuntimeException("Company not found") }
        return toResponse(company)
    }


    private fun toResponse(company: Company): CompanyResponse {
        return CompanyResponse(
            id = company.id!!,
            name = company.name,
            description = company.description,
            size = company.size,
            requiredSkills = company.requiredSkills,
            preferredSkills = company.preferredSkills,
            location = company.location,
            websiteUrl = company.websiteUrl
        )
    }
}
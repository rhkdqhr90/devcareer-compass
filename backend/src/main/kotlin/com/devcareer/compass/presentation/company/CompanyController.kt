package com.devcareer.compass.presentation.company

import com.devcareer.compass.application.common.ApiResponse
import com.devcareer.compass.application.company.dto.CompanyRecommendation
import com.devcareer.compass.application.company.dto.CompanyRequest
import com.devcareer.compass.application.company.dto.CompanyResponse
import com.devcareer.compass.application.company.service.CompanyRecommendationService
import com.devcareer.compass.application.company.service.CompanyService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/companies")
class CompanyController(
    private val companyService: CompanyService,
    private val recommendationService: CompanyRecommendationService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun createCompany(
        @RequestBody request: CompanyRequest
    ): ResponseEntity<ApiResponse<CompanyResponse>> {
        logger.info("Creating company: ${request.name}")

        val company = companyService.createCompany(request)
        return ResponseEntity.ok(ApiResponse.success(company))
    }

    @GetMapping
    fun getAllCompanies(): ResponseEntity<ApiResponse<List<CompanyResponse>>> {
        val companies = companyService.gerAllCompanies()
        return ResponseEntity.ok(ApiResponse.success(companies))
    }

    @GetMapping("/{id}")
    fun getCompany(@PathVariable id: Long): ResponseEntity<ApiResponse<CompanyResponse>> {
        val company = companyService.getCompany(id)
        return ResponseEntity.ok(ApiResponse.success(company))
    }

    @GetMapping("/recommend/{resumeId}")
    fun recommendCompanies(
        @PathVariable resumeId: Long,
        @RequestParam(defaultValue = "5") limit: Int
    ): ResponseEntity<ApiResponse<List<CompanyRecommendation>>>{
        logger.info("Recommend companies for resume: $resumeId")
        val recommendations = recommendationService.recommendCompanies(resumeId, limit)
        return ResponseEntity.ok(ApiResponse.success(recommendations))

    }

}
package com.devcareer.compass.application.company.dto

import com.devcareer.compass.application.positioning.service.SkillMatch
import com.devcareer.compass.domain.company.CompanySize

data class CompanyRequest(
    val name: String,
    val description: String,
    val size: CompanySize,
    val requiredSkills: List<String>,
    val preferredSkills: List<String> = emptyList(),
    val location: String?,
    val websiteUrl: String?
)

data class CompanyResponse(
    val id: Long,
    val name: String,
    val description: String,
    val size: CompanySize,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val location: String?,
    val websiteUrl: String?
)

data class CompanyRecommendation(
    val company: CompanyResponse,
    val matchScore: Double,
    val matchSkills: List<String>,
    val missingSkills: List<String>,
    val reason: String
    )

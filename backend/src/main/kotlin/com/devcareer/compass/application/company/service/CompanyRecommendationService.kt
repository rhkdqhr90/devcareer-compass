package com.devcareer.compass.application.company.service

import com.devcareer.compass.application.company.dto.CompanyRecommendation
import com.devcareer.compass.application.resume.dto.ParsedResumeData
import com.devcareer.compass.domain.company.Company
import com.devcareer.compass.infrastructure.persistence.company.CompanyRepository
import com.devcareer.compass.infrastructure.persistence.resume.ResumeJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.util.Locale

@Service
@Transactional(readOnly = true)
class CompanyRecommendationService(
    private val companyRepository: CompanyRepository,
    private val resumeRepository: ResumeJpaRepository,
    private val companyService: CompanyService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun recommendCompanies(resumeId: Long, limit: Int = 5): List<CompanyRecommendation>{
        logger.info("Recommend companies for resume: $resumeId")

        val resume = resumeRepository.findById(resumeId).orElseThrow { RuntimeException("Resume not found") }

        val parsedData = objectMapper.readValue(
            resume.parsedDataJson,
            ParsedResumeData::class.java
        )

        val userSkills = parsedData.skills.map { it.lowercase(Locale.getDefault()) }.toSet()

        val companies = companyRepository.findAll()

        val recommendations = companies.map { company ->
            calculateMatch(company, userSkills)
        }

        return recommendations.sortedByDescending { it.matchScore }.take(limit)
    }

    private fun calculateMatch(
        company: Company,
        userSkills: Set<String>
    ): CompanyRecommendation {
        val requiredSkills = company.requiredSkills.map { it.lowercase(Locale.getDefault()) }
        val preferredSkills = company.preferredSkills.map { it.lowercase(Locale.getDefault()) }


        // 필수 스킬 매칭
        val matchedRequired = requiredSkills.filter { userSkills.contains(it) }
        val requiredMatchRate = if (requiredSkills.isNotEmpty()) {
            (matchedRequired.size.toDouble() / requiredSkills.size) * 100
        } else {
            100.0
        }

        // 우대 스킬 매칭
        val matchedPreferred = preferredSkills.filter { userSkills.contains(it) }
        val preferredBonus = if (preferredSkills.isNotEmpty()) {
            (matchedPreferred.size.toDouble() / preferredSkills.size) * 20  // 최대 20점 가산
        } else {
            0.0
        }

        //최종 점수 필요스킬 80% 선호스킬 20%
        val matchScore = minOf(100.0, requiredMatchRate * 0.8 + preferredBonus)

        val matchedSkills = (matchedRequired + matchedPreferred).distinct()

        //부족한 스킬
        val missingSkills = requiredSkills.filter { !matchedSkills.contains(it) }

        //추천 이유
        val reason = buildReason(requiredMatchRate, matchedSkills.size, missingSkills.size)

        return CompanyRecommendation(
            company = companyService.getCompany(company.id!!),
            matchScore = matchScore,
            matchSkills = matchedSkills,
            missingSkills = missingSkills,
            reason = reason
        )

    }

    private fun buildReason(requiredMatchRate: Double, matchedCount: Int, missingCount: Int): String {
        return when {
            requiredMatchRate >= 80 -> "필수 스킬 ${matchedCount}개 보유, 매우 적합합니다"
            requiredMatchRate >= 60 -> "필수 스킬 ${matchedCount}개 보유, 적합합니다"
            requiredMatchRate >= 40 -> "필수 스킬 ${missingCount}개 부족하지만 도전해볼 만합니다"
            else -> "필수 스킬 ${missingCount}개 부족, 학습 후 지원 권장"
        }
    }
}
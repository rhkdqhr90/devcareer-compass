package com.devcareer.compass.application.positioning.service

import com.devcareer.compass.application.market.service.MarketStatisticsService
import com.devcareer.compass.application.resume.dto.ParsedResumeData
import com.devcareer.compass.infrastructure.persistence.resume.ResumeJpaRepository
import com.devcareer.compass.presentation.exception.ResumeNotFoundException

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import tools.jackson.databind.ObjectMapper
import java.util.Locale

@Service
@Transactional(readOnly = true)
class ResumePositioningService(
    private val resumeRepository: ResumeJpaRepository,
    private val marketStatisticsService: MarketStatisticsService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun analyzePositioning(resumeId: Long): PositioningResult{

        //이력서 가져오기
        val resume = resumeRepository.findById(resumeId).orElseThrow { ResumeNotFoundException(resumeId) }

        val parsedData = objectMapper.readValue(
            resume.parsedDataJson,
            ParsedResumeData::class.java
        )

        val userSkills = parsedData.skills.map { it.lowercase(Locale.getDefault()) }.toSet()

        //통계 가져오기
        val marketStats = marketStatisticsService.getMarketStatistics()

        //매칭 분석
        val matchedSkills = mutableListOf<SkillMatch>()
        val missingSkills = mutableListOf<SkillMatch>()

        marketStats.forEach { stat ->
            val skillMatch = SkillMatch(
                skillName = stat.skillName,
                percentage = stat.percentage,
                hasSkill = userSkills.contains(stat.skillName.lowercase())
            )
            if(skillMatch.hasSkill) matchedSkills.add(skillMatch) else missingSkills.add(skillMatch)
        }
        // 4. 백분위 계산
        val totalMarketSkills = marketStats.size
        val userMatchCount = matchedSkills.size
        val percentile = if (totalMarketSkills > 0) {
            (userMatchCount.toDouble() / totalMarketSkills) * 100
        } else {
            0.0
        }

        return PositioningResult(
            resumeId = resumeId,
            userSkills = parsedData.skills,
            matchedSkills = matchedSkills.sortedByDescending { it.percentage },
            missingSkills = missingSkills
                .sortedByDescending { it.percentage }
                .take(5),  // 상위 5개만
            percentile = percentile,
            matchRate = (userMatchCount.toDouble() / totalMarketSkills) * 100
        )

    }

}

data class PositioningResult(
    val resumeId: Long,
    val userSkills: List<String>,
    val matchedSkills: List<SkillMatch>,
    val missingSkills: List<SkillMatch>,
    val percentile: Double,
    val matchRate: Double
)

data class SkillMatch(
    val skillName: String,
    val percentage: Double,
    val hasSkill: Boolean
)
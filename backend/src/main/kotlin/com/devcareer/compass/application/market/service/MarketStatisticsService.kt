package com.devcareer.compass.application.market.service

import com.devcareer.compass.application.market.dto.MarketStatisticsRequest
import com.devcareer.compass.application.market.dto.MarketStatisticsResponse
import com.devcareer.compass.domain.market.MarketStatistics
import com.devcareer.compass.infrastructure.persistence.market.MarketStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MarketStatisticsService(
    private val marketStatisticsRepository: MarketStatisticsRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @Transactional
    fun saveMarketStatistics(request: MarketStatisticsRequest): List<MarketStatisticsResponse> {
        logger.info("Saving market statistics: totalJobs=${request.totalJobs}, skills=${request.skills.size}")

        // 기존 데이터 삭제 (최신 데이터만 유지)
        marketStatisticsRepository.deleteAll()

        // 새 통계 저장
        val statistics = request.skills.map { (skillName, frequency) ->
            val percentage = (frequency.toDouble() / request.totalJobs) * 100
            MarketStatistics(
                skillName = skillName,
                frequency = frequency,
                totalJobs = request.totalJobs,
                percentage = percentage
            )
        }

        val saved = marketStatisticsRepository.saveAll(statistics)

        logger.info("Saved ${saved.size} market statistics")

        return saved.map { toResponse(it) }
            .sortedByDescending { it.percentage }
    }

    fun getMarketStatistics(): List<MarketStatisticsResponse> {
        return marketStatisticsRepository.findAll()
            .map { toResponse(it) }
            .sortedByDescending { it.percentage }
    }

    private fun toResponse(entity: MarketStatistics): MarketStatisticsResponse {
        return MarketStatisticsResponse(
            skillName = entity.skillName,
            frequency = entity.frequency,
            totalJobs = entity.totalJobs,
            percentage = entity.percentage
        )
    }
}
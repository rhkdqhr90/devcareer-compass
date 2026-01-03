package com.devcareer.compass.infrastructure.persistence.market

import com.devcareer.compass.domain.market.MarketStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketStatisticsRepository: JpaRepository<MarketStatistics, Long> {
    fun findBySource(source: String): List<MarketStatistics>
    fun findBySourceOrderByPercentageDesc(source: String): List<MarketStatistics>
    fun findBySkillName(skillName: String): MarketStatistics?

}
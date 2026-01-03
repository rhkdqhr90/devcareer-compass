package com.devcareer.compass.domain.market

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "market_statistics")
class MarketStatistics(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val skillName: String,
    val frequency: Int,  // 몇 개 공고에 나왔나
    val totalJobs: Int,  // 전체 공고 수
    val percentage: Double,  // 비율

    val source: String = "manual",  // "manual", "saramin", "wanted"
    val category: String? = null,  // "backend", "frontend", "devops"

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun calculatePercentage(): Double {
        return if (totalJobs > 0) {
            (frequency.toDouble() / totalJobs) * 100
        } else {
            0.0
        }
    }
}
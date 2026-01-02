package com.devcareer.compass.application.market.dto

data class MarketStatisticsRequest(
    val totalJobs: Int,  // 전체 공고 수
    val skills: Map<String, Int>  // {"Java": 85, "Spring": 80}
     )
data class MarketStatisticsResponse(
    val skillName: String,
    val frequency: Int,
    val totalJobs: Int,
    val percentage: Double
)
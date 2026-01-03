package com.devcareer.compass.presentation.market

import com.devcareer.compass.application.common.ApiResponse
import com.devcareer.compass.application.market.dto.MarketStatisticsRequest
import com.devcareer.compass.application.market.dto.MarketStatisticsResponse
import com.devcareer.compass.application.market.service.MarketStatisticsService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/market")
class MarketStatisticsController(
    private val marketStatisticsService: MarketStatisticsService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @PostMapping("/statistics")
    fun saveMarketStatistics(
        @RequestBody request: MarketStatisticsRequest
    ): ResponseEntity<ApiResponse<List<MarketStatisticsResponse>>> {
        logger.info("Save market statistics: totalJobs=${request.totalJobs}")

        val response = marketStatisticsService.saveMarketStatistics(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/statistics")
    fun getMarketStatistics(): ResponseEntity<ApiResponse<List<MarketStatisticsResponse>>> {
        val response = marketStatisticsService.getMarketStatistics()
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
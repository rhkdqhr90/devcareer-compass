package com.devcareer.compass.infrastructure.ai

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class OpenAIClient(
    @Value("\${openai.api-key}")
    private val apiKey: String,

    @Value("\${openai.model}")
    private val model: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val webClient = WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun chat(systemPrompt: String, userMessage: String): String {
        logger.info("Sending request to OpenAI (model: $model)")

        val request = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf("role" to "system", "content" to systemPrompt),
                mapOf("role" to "user", "content" to userMessage)
            ),
            "temperature" to 0.7,
            "max_tokens" to 2000
        )

        return try {
            val response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            @Suppress("UNCHECKED_CAST")
            val choices = response?.get("choices") as? List<Map<String, Any>>
            val message = choices?.firstOrNull()?.get("message") as? Map<String, Any>
            val content = message?.get("content") as? String ?: "응답 없음"

            logger.info("OpenAI response received (${content.length} chars)")
            content
        } catch (e: Exception) {
            logger.error("OpenAI API error", e)
            throw RuntimeException("AI 분석 실패: ${e.message}", e)
        }
    }
}
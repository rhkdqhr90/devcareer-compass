package com.devcareer.compass.infrastructure.ai

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OllamaClient(
    @Value("\${ollama.base-url}")
    private val baseUrl: String,

    @Value("\${ollama.model}")
    private val model: String,

 ) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val webClient: WebClient = WebClient.builder().baseUrl(baseUrl).build()
    fun chat(prompt: String): String {
        logger.info("Sending prompt to Ollama: ${prompt.take(100)}...")


        val request = mapOf(
            "model" to model,
            "prompt" to prompt,
            "stream" to false
        )

        return try {
            val response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            response?.get("response")?.toString() ?: "응답 없음"
        } catch (e: Exception) {
            logger.error("Ollama API error", e)
            "AI 분석 실패: ${e.message}"
        }

    }

}

data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

data class OllamaResponse(
    val response: String,
    val done: Boolean = false
)
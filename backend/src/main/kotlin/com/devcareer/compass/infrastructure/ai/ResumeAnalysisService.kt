package com.devcareer.compass.infrastructure.ai

import com.devcareer.compass.application.resume.dto.ParsedResumeData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class ResumeAnalysisService(
    private val ollamaClient: OllamaClient,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun analyzeResume(rawText: String): ParsedResumeData {
        logger.info("Analyzing resume with AI...")

        val prompt = buildPrompt(rawText)
        val aiResponse = ollamaClient.chat(prompt)

        return try {
            parsAIResponse(aiResponse, rawText)
        }catch (e: Exception){
            logger.error("Failed to parse AI response", e)
            // AI 파싱 실패 시 기본값
            ParsedResumeData(rawText = rawText)
        }
    }

    private fun parsAIResponse(
        aiResponse: String,
        rawText: String
    ): ParsedResumeData {
        logger.info("AI Response: ${aiResponse.take(200)}...")

        val jsonText = aiResponse.replace("```json","").replace("```","").trim()

        return try{
            val parsed = objectMapper.readValue(jsonText, ParsedResumeData::class.java)
            parsed
        }catch (e: Exception){
            logger.error("Failed to parse AI response JSON", e)
            ParsedResumeData(rawText = rawText)
        }
    }

    private fun buildPrompt(rawText: String):String {
            return """
              다음 이력서에서 정보를 추출해주세요. 반드시 JSON 형식으로만 답변하세요.
              이력서:$rawText

              다음 JSON 형식으로 답변하세요:
              {
                "name": "이름",
                "email": "이메일",
                "phone": "전화번호",
                "skills": ["기술1", "기술2"],
                "experiences": ["경력1", "경력2"],
                "educations": ["학력1", "학력2"]
              }

              JSON만 출력하고 다른 설명은 하지 마세요.
            """.trimIndent()
    }
}

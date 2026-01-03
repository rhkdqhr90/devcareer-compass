package com.devcareer.compass.application.learning.service


import com.devcareer.compass.application.learning.dto.LearningRoadmap
import com.devcareer.compass.application.positioning.service.ResumePositioningService
import com.devcareer.compass.application.resume.dto.ParsedResumeData
import com.devcareer.compass.infrastructure.persistence.resume.ResumeJpaRepository
import com.devcareer.compass.infrastructure.ai.OpenAIClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@Service
@Transactional(readOnly = true)
class LearningRoadmapService(
    private val resumeRepository: ResumeJpaRepository,
    private val resumePositioningService: ResumePositioningService,
    private val objectMapper: ObjectMapper,
    private val openAIClient: OpenAIClient
) {

    private val logger = LoggerFactory.getLogger(javaClass)


    fun generateRoadmap(resumeId: Long): LearningRoadmap {
        logger.info("Generating learning roadmap for resume: $resumeId")

        // 포지셔닝 분석 결과 가져오기
        val positioning = resumePositioningService.analyzePositioning(resumeId)

        //사용자 보유 스킬
        val resume = resumeRepository.findById(resumeId).orElseThrow { RuntimeException("Resume not found") }

        val parsedData = objectMapper.readValue(
            resume.parsedDataJson,
            ParsedResumeData::class.java
        )

        val roadmapJson = generateRoadmapWithAI(userSkills = parsedData.skills, missingSkills = positioning.missingSkills)

        return parseRoadmapJson(roadmapJson, resumeId)

    }



    private fun generateRoadmapWithAI(
        userSkills: List<String>,
        missingSkills: List<com.devcareer.compass.application.positioning.service.SkillMatch>
    ): String {
        val systemPrompt = """
        당신은 개발자 커리어 코치입니다.
        사용자의 현재 스킬과 부족한 스킬을 분석하여 개인화된 학습 로드맵을 만들어주세요.
        반드시 JSON 형식으로만 답변하세요.
        """.trimIndent()

        val userMessage = """
        **현재 보유 스킬:**
        ${userSkills.joinToString(", ")}
        
        **부족한 스킬 (시장 수요):**
        ${missingSkills.joinToString("\n") { "- ${it.skillName} (${it.percentage.toInt()}%)" }}
        
        다음 JSON 형식으로 학습 로드맵을 작성해주세요:
        
        {
          "recommendedSkills": [
            {
              "skillName": "Spring Boot",
              "priority": 5,
              "marketDemand": 75.0,
              "difficulty": "보통",
              "prerequisitesMet": true,
              "reason": "높은 시장 수요(75%), Java/Spring 보유로 바로 학습 가능"
            }
          ],
          "learningPath": [
            {
              "order": 1,
              "skillName": "Spring Boot",
              "estimatedWeeks": 4,
              "resources": [
                {
                  "type": "강의",
                  "title": "스프링 부트 핵심 가이드",
                  "url": "https://inflearn.com/course/spring-boot",
                  "provider": "인프런"
                }
              ],
              "prerequisites": ["Java", "Spring"]
            }
          ],
          "estimatedTotalWeeks": 12
        }
        
        **요구사항:**
        1. priority는 1~5 (시장 수요와 선수 스킬 고려)
        2. difficulty는 "쉬움", "보통", "어려움"
        3. 실제 존재하는 강의/책 추천
        4. 학습 순서는 선수 스킬 고려
        5. 상위 5개 스킬만 포함
         """.trimIndent()

        return openAIClient.chat(systemPrompt, userMessage)
    }

    private fun parseRoadmapJson(json: String, resumeId: Long): LearningRoadmap {
        return try {
            val cleanJson = json
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val roadmap = objectMapper.readValue(cleanJson, LearningRoadmap::class.java)
            roadmap.copy(resumeId = resumeId)  // resumeId 설정!
        } catch (e: Exception) {
            logger.error("Failed to parse AI roadmap", e)
            LearningRoadmap(
                resumeId = resumeId,
                recommendedSkills = emptyList(),
                learningPath = emptyList(),
                estimatedTotalWeeks = 0
            )
        }

    }


}

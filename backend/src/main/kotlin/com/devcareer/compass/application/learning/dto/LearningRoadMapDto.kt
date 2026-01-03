package com.devcareer.compass.application.learning.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LearningRoadmap(
    val resumeId: Long =0,
    val recommendedSkills: List<SkillRecommendation> = emptyList(),
    val learningPath: List<LearningStep> = emptyList(),
    val estimatedTotalWeeks: Int = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SkillRecommendation(
    val skillName: String = "",
    val priority: Int = 0,
    val marketDemand: Double = 0.0,
    val difficulty: String = "",
    val prerequisitesMet: Boolean = false,
    val reason: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LearningStep(
    val order: Int = 0,
    val skillName: String = "",
    val estimatedWeeks: Int = 0,
    val resources: List<LearningResource> = emptyList(),
    val prerequisites: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LearningResource(
    val type: String = "",
    val title: String = "",
    val url: String? = null,
    val provider: String? = null
)

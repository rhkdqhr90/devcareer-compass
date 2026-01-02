package com.devcareer.compass.application.jobposting.dto


data class ParsedJobData(
    val jobs : List<JobInfo> = emptyList(),
    val totalCount: Int = 0
)
data class JobInfo(
    val title: String,
    val company: String,
    val requiredSkills: List<String> = emptyList(),
    val preferredSkills: List<String> = emptyList(),
    val experienceYears: String? = null
)
data class SkillStatistics(
    val skillName: String,
    val frequency: Int,
    val percentage: Double
)
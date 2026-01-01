package com.devcareer.compass.application.resume.dto

data class ParsedResumeData(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val skills: List<String> = emptyList(),
    val experiences : List<String> = emptyList(),
    val educations : List<String> = emptyList(),
    val rawText: String?
) {
}
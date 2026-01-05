package com.devcareer.compass.domain.company

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "companies")
class Company(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String,

    @Enumerated(EnumType.STRING)
    val size: CompanySize,

    @ElementCollection
    @CollectionTable(name ="company_required_skills", joinColumns = [JoinColumn(name = "company_id")])
    @Column(name = "skill")
    val requiredSkills: List<String> = emptyList(),

    @ElementCollection
    @CollectionTable(name ="company_preferred_skills", joinColumns = [JoinColumn(name = "company_id")])
    @Column(name = "skill")
    val preferredSkills: List<String> = emptyList(),

    val location: String? = null,
    val websiteUrl: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

enum class CompanySize { STARTUP, MEDIUM, LARGE }


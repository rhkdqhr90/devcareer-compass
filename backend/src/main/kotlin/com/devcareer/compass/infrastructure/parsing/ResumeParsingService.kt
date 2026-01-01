package com.devcareer.compass.infrastructure.parsing

import com.devcareer.compass.application.resume.dto.ParsedResumeData
import com.devcareer.compass.domain.resume.FileType
import com.devcareer.compass.presentation.exception.FileParsingFailedException
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream

@Service
class ResumeParsingService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun parseResume(filePath: String, fileType: FileType): ParsedResumeData{
        logger.info("Parsing resume file: $filePath, type: $fileType")

        val rawText = when (fileType) {
            FileType.PDF-> extractTextFromPdf(filePath)
            FileType.DOCX-> extractTextFromDocx(filePath)
            else -> throw FileParsingFailedException("지원하지 않는 파일 타입: $fileType")
        }
        return parseText(rawText)

    }

    private fun parseText(rawText: String): ParsedResumeData {
        val name = extractName(rawText)
        val email = extractPhone(rawText)
        val phone = extractEmail(rawText)
        val skills = extractSkills(rawText)
        return ParsedResumeData(name, email, phone, skills, rawText = rawText)
    }
    private fun extractName(text: String): String? {
        val pattern = Regex("""이름\s*[:：]?\s*([가-힣]{2,4})""")
        return pattern.find(text)?.groupValues?.get(1)
    }
    private fun extractEmail(text: String): String? {
        // 이메일 패턴
        val pattern = Regex("""([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})""")
        return pattern.find(text)?.groupValues?.get(1)
    }

    private fun extractPhone(text: String): String? {
        val pattern = Regex("""(\d{2,3}[-.]?\d{3,4}[-.]?\d{4})""")
        return pattern.find(text)?.groupValues?.get(1)
    }
    private fun extractSkills(text: String): List<String> {
        // 기술 스택 키워드
        val keywords = listOf(
            "Java", "Kotlin", "Spring", "JavaScript", "Python",
            "React", "Vue", "Node", "Docker", "Kubernetes",
            "AWS", "MySQL", "PostgreSQL", "Redis", "MongoDB"
        )

        return keywords.filter { text.contains(it, ignoreCase = true) }
    }

    private fun extractTextFromDocx(filePath: String): String {
        try {
            FileInputStream(filePath).use {fis ->
                XWPFDocument(fis).use { doc ->
                    return doc.paragraphs.joinToString("\n") { it.text }
                }
            }
        }catch (e: Exception){
            logger.error("Failed to parse DOCX: $filePath",e)
            throw FileParsingFailedException("DOCX 파실 실패: ${e.message}")
        }
    }

    private fun extractTextFromPdf(filePath: String):String {
        try{
            val document = org.apache.pdfbox.Loader.loadPDF(File(filePath))
            document.use {
                val stripper = PDFTextStripper()
                return stripper.getText(it)
            }
        }catch (e: Exception) {
            logger.error("Failed to parse PDF: $filePath", e)
            throw FileParsingFailedException("PDF 파싱 실패: ${e.message}")
        }
    }
}
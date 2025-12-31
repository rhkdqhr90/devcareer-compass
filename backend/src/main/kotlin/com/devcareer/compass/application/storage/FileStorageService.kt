package com.devcareer.compass.application.storage

import com.devcareer.compass.domain.resume.FileType
import com.devcareer.compass.presentation.exception.FileSaveException
import com.devcareer.compass.presentation.exception.InvalidFileTypeException
import org.springframework.beans.factory.annotation.Value
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class FileStorageService(
    @Value("\${storage.upload-dir}")
    private val uploadDir: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val uploadPath: Path = Paths.get(uploadDir)

    init {
        try {
            Files.createDirectories(uploadPath)
            logger.info("Upload directory created: $uploadPath")
        }catch (e: IOException){
            throw FileSaveException("Failed to create upload directory: ${e.message}")
        }
    }

    fun store(file: MultipartFile): StoredFileInfo{
        val fileType = validateAndGetFileType(file)

        val originalFileName = file.originalFilename ?: throw FileSaveException("파일명이 없습니다.")
        val extension = getFileExtension(originalFileName)
        val storedFileName = "${UUID.randomUUID()}.$extension"

        try {
            val targetPath = uploadPath.resolve(storedFileName)
            Files.copy(file.inputStream, targetPath)

            logger.info("File stored: $storedFileName (origin: $originalFileName)")

            return StoredFileInfo(
                fileName = storedFileName,
                originalFileName = originalFileName,
                filePath = targetPath.toString(),
                fileSize = file.size,
                fileType = fileType
            )
        }catch (e: IOException){
            logger.error("파일 저장 실패 $originalFileName",e)
            throw FileSaveException("파일 저장 실패 :${e.message}")

        }

    }

    private fun validateAndGetFileType(file: MultipartFile):FileType {
        val originalFileName = file.originalFilename ?: throw IllegalArgumentException("File name is empty")
        val extension = getFileExtension(originalFileName).lowercase()

        return when(extension){
            "pdf" -> FileType.PDF
            "docx" -> FileType.DOCX
            else -> throw InvalidFileTypeException()
        }
    }

    private fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        if(lastDotIndex == -1) throw InvalidFileTypeException()
        return fileName.substring(lastDotIndex + 1)
    }
}

data class StoredFileInfo(
    val fileName: String,
    val originalFileName: String,
    val filePath: String,
    val fileSize: Long,
    val fileType: FileType
)
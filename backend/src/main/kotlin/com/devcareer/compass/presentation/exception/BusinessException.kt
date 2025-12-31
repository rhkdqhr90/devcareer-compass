package com.devcareer.compass.presentation.exception

import com.devcareer.compass.application.common.ErrorCode

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String =errorCode.message,
    val details: Any? = null
): RuntimeException(message)

class FileRequiredException: BusinessException(ErrorCode.FILE_REQUIRED)
class InvalidFileTypeException: BusinessException(ErrorCode.INVALID_FILE_TYPE)
class FileSizeExceededException: BusinessException(ErrorCode.FILE_SIZE_EXCEEDED)
class FileParsingFailedException(details: Any? = null): BusinessException(ErrorCode.FILE_PARSING_FAILED, details = details)
class FileSaveException(message: String? = null) : BusinessException(  // ← 이거 추가!
    ErrorCode.FILE_SAVE_FAILED,
    message = message ?: ErrorCode.FILE_SAVE_FAILED.message
)
class ResumeNotFoundException(id: Long? = null): BusinessException(ErrorCode.RESUME_NOT_FOUND, details = id?.let {mapOf("id" to it)})
class AnalysisNotFoundException(id: Long? = null): BusinessException(ErrorCode.ANALYSIS_NOT_FOUND, details = id?.let {mapOf("analysisId" to it)})
class AnalysisInProgressException(analysisId: Long): BusinessException(ErrorCode.ANALYSIS_IN_PROGRESS,details = mapOf("existingAnalysisId" to analysisId))
class ResumeNotParsedException: BusinessException(ErrorCode.RESUME_NOT_PARSED)

class AIServiceException(message: String? = null): BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, message = message ?: ErrorCode.AI_SERVICE_UNAVAILABLE.message)

class OllamaConnectionException: BusinessException(ErrorCode.OLLAMA_CONNECTION_FAILED)


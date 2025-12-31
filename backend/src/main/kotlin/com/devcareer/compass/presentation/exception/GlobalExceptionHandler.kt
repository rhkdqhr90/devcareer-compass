package com.devcareer.compass.presentation.exception

import com.devcareer.compass.application.common.ApiResponse
import com.devcareer.compass.application.common.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Business Exception: ${ex.errorCode.name} - ${ex.message}",ex)

        val response = ApiResponse.error<Nothing>(
            code = ex.errorCode.name,
            message = ex.message,
            details = ex.details
        )
        val status = when(ex.errorCode){
            ErrorCode.RESUME_NOT_FOUND,
            ErrorCode.ANALYSIS_NOT_FOUND -> HttpStatus.NOT_FOUND

            ErrorCode.FILE_REQUIRED,
            ErrorCode.INVALID_FILE_TYPE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.ANALYSIS_IN_PROGRESS -> HttpStatus.BAD_REQUEST

            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity.status(status).body(response)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceeded(ex: MaxUploadSizeExceededException): ResponseEntity<ApiResponse<Nothing>>{
        logger.warn("File size exceeded", ex)

        val response = ApiResponse.error<Nothing>(
            code = ErrorCode.FILE_SIZE_EXCEEDED.name,
            message = ErrorCode.FILE_SIZE_EXCEEDED.message,
            details = mapOf("maxUploadSize" to "10MB")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex:Exception): ResponseEntity<ApiResponse<Nothing>>{
        logger.error("General Exception", ex)

        val response = ApiResponse.error<Nothing>(
            code = ErrorCode.INTERNAL_SERVER_ERROR.name,
            message = ErrorCode.INTERNAL_SERVER_ERROR.message
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }



}
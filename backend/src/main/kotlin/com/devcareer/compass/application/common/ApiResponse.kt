package com.devcareer.compass.application.common

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val timeStamp: Instant = Instant.now()
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data
            )
        }
        fun <T> error(code: String, message: String, details: Any? = null ): ApiResponse<T>{
            return ApiResponse(
                success = false,
                error = ErrorDetail(code = code, message = message, details = details)
            )
        }
    }
}
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Any? = null
)
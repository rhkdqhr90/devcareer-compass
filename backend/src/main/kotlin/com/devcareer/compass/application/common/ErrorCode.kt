package com.devcareer.compass.application.common

enum class ErrorCode(val message: String) {
    // 파일 관련
    FILE_REQUIRED("파일을 선택해주세요"),
    INVALID_FILE_TYPE("PDF 또는 DOCX 파일만 업로드 가능합니다"),
    FILE_SIZE_EXCEEDED("파일 크기는 10MB를 초과할 수 없습니다"),
    FILE_PARSING_FAILED("파일 파싱에 실패했습니다"),
    FILE_SAVE_FAILED("파일 저장에 실패했습니다"),

    // 리소스 관련
    RESUME_NOT_FOUND("존재하지 않는 이력서입니다"),
    ANALYSIS_NOT_FOUND("존재하지 않는 분석 결과입니다"),

    // 비즈니스 로직
    ANALYSIS_IN_PROGRESS("이미 분석이 진행 중입니다"),
    RESUME_NOT_PARSED("이력서가 아직 파싱되지 않았습니다"),

    // 외부 서비스
    AI_SERVICE_UNAVAILABLE("AI 서비스를 사용할 수 없습니다"),
    OLLAMA_CONNECTION_FAILED("Ollama 연결에 실패했습니다"),

    // 일반
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다"),
    VALIDATION_FAILED("입력값 검증에 실패했습니다");
}
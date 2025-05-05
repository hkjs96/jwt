package com.example.jwt.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    @Schema(description = "응답 코드 (" +
            "SUCCESS: 성공, ERROR: 실패")
    private final String code;
    @Schema(description = "응답 메시지")
    private final String message;
    @Schema(description = "응답 데이터")
    private final T data;

    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "OK", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>("SUCCESS", "OK", null);
    }

    public static ApiResponse<Void> error(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}
package org.ichwan.dto;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    // Factory methods for clean usage in resources
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static ApiResponse<Void> created(String message) {
        return new ApiResponse<>(201, message, null);
    }
}

package com.delivery_api.Projeto.Delivery.API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private boolean success = false;
    private ErrorDetail error;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private String details;
    }

    public static ErrorResponse of(String code, String message, String details) {
        ErrorDetail errorDetail = new ErrorDetail(code, message, details);
        return new ErrorResponse(false, errorDetail, LocalDateTime.now());
    }
}


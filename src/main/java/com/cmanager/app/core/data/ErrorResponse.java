package com.cmanager.app.core.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private String path;
    private Integer status;
    private String error;
    private final LocalDateTime timestamp = LocalDateTime.now();
}

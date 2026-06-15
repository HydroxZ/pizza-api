package com.awesomepizza.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrapper for API error responses.
 */
@Data
@AllArgsConstructor
public class ApiError {
    private final String code;
    private final String message;
    private final long timestamp;
}

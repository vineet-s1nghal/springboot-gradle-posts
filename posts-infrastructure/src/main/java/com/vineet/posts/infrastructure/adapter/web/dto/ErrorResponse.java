package com.vineet.posts.infrastructure.adapter.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure.
 * 
 * Having a consistent error format helps API consumers:
 *   - Always know what structure to expect
 *   - Parse errors programmatically
 *   - Display meaningful messages to users
 * 
 * =============================================================================
 * ERROR RESPONSE FORMAT
 * =============================================================================
 * 
 * {
 *   "timestamp": "2024-01-15T10:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Post not found with id: 550e8400-...",
 *   "path": "/api/posts/550e8400-...",
 *   "fieldErrors": null
 * }
 * 
 * For validation errors:
 * {
 *   "timestamp": "2024-01-15T10:30:00",
 *   "status": 400,
 *   "error": "Validation Failed",
 *   "message": "Request validation failed",
 *   "path": "/api/posts",
 *   "fieldErrors": [
 *     {"field": "title", "message": "Title is required"},
 *     {"field": "content", "message": "Content is required"}
 *   ]
 * }
 */
public record ErrorResponse(
        
        /**
         * When the error occurred.
         */
        LocalDateTime timestamp,
        
        /**
         * HTTP status code (e.g., 400, 404, 500).
         */
        int status,
        
        /**
         * Short error description (e.g., "Not Found", "Bad Request").
         */
        String error,
        
        /**
         * Detailed error message.
         */
        String message,
        
        /**
         * The request path that caused the error.
         */
        String path,
        
        /**
         * Field-level validation errors (null if not a validation error).
         */
        List<FieldError> fieldErrors
        
) {
    
    /**
     * Nested record for field-level validation errors.
     */
    public record FieldError(
            /**
             * The field name that failed validation.
             */
            String field,
            
            /**
             * The validation error message.
             */
            String message
    ) {}
    
    /**
     * Factory method for simple errors (no field errors).
     * 
     * Usage:
     *   ErrorResponse.of(404, "Not Found", "Post not found", "/api/posts/123")
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                null
        );
    }
    
    /**
     * Factory method for validation errors (with field errors).
     * 
     * Usage:
     *   ErrorResponse.ofValidation(400, "Validation Failed", "...", "/api/posts", fieldErrors)
     */
    public static ErrorResponse ofValidation(int status, String error, String message,
                                              String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                fieldErrors
        );
    }
}

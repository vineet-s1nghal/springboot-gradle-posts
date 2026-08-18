package com.vineet.posts.infrastructure.adapter.web.exception;

import com.vineet.posts.application.exception.PostNotFoundException;
import com.vineet.posts.infrastructure.adapter.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for the REST API.
 * 
 * =============================================================================
 * WHAT IS @RestControllerAdvice?
 * =============================================================================
 * 
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * 
 * It allows you to handle exceptions across ALL controllers in one place.
 * Without it, you'd need try-catch in every controller method!
 * 
 * BEFORE (without global handler):
 * 
 *   @GetMapping("/{id}")
 *   public ResponseEntity<?> getPost(@PathVariable UUID id) {
 *       try {
 *           Post post = getPostUseCase.execute(id);
 *           return ResponseEntity.ok(PostResponse.from(post));
 *       } catch (PostNotFoundException e) {
 *           return ResponseEntity.status(404).body(errorResponse);
 *       } catch (Exception e) {
 *           return ResponseEntity.status(500).body(errorResponse);
 *       }
 *   }
 * 
 * AFTER (with global handler):
 * 
 *   @GetMapping("/{id}")
 *   public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
 *       Post post = getPostUseCase.execute(id);  // Just let it throw!
 *       return ResponseEntity.ok(PostResponse.from(post));
 *   }
 * 
 * =============================================================================
 * @ExceptionHandler
 * =============================================================================
 * 
 * @ExceptionHandler(SomeException.class) marks a method that handles that exception.
 * Spring automatically routes exceptions to the matching handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // =========================================================================
    // BUSINESS EXCEPTIONS
    // =========================================================================
    
    /**
     * Handle PostNotFoundException → HTTP 404 Not Found.
     * 
     * Thrown when:
     *   - GetPostUseCase can't find a post
     *   - UpdatePostUseCase can't find a post to update
     *   - DeletePostUseCase can't find a post to delete
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(
            PostNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Post not found: {}", ex.getPostId());
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // =========================================================================
    // VALIDATION EXCEPTIONS
    // =========================================================================
    
    /**
     * Handle validation errors → HTTP 400 Bad Request.
     * 
     * Thrown when @Valid fails on request body.
     * 
     * Example trigger:
     *   POST /api/posts with body {"title": "", "content": ""}
     *   → title and content are @NotBlank
     *   → MethodArgumentNotValidException is thrown
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // Extract field-level errors
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(
                        fe.getField(),
                        fe.getDefaultMessage()
                ))
                .toList();
        
        log.warn("Validation failed for {}: {}", request.getRequestURI(), fieldErrors);
        
        ErrorResponse error = ErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Request validation failed. Check 'fieldErrors' for details.",
                request.getRequestURI(),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    // =========================================================================
    // CATCH-ALL HANDLER
    // =========================================================================
    
    /**
     * Handle all other unexpected exceptions → HTTP 500 Internal Server Error.
     * 
     * This is a safety net for unexpected errors.
     * We log the full exception for debugging but return a generic message
     * to the client (don't leak internal details!).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        // Log full stack trace for debugging
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        // Return generic message (don't expose internal details!)
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

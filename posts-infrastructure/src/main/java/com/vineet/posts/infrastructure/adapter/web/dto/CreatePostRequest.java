package com.vineet.posts.infrastructure.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new post.
 * 
 * =============================================================================
 * WHAT IS A DTO?
 * =============================================================================
 * 
 * DTO = Data Transfer Object
 * 
 * A simple object that carries data between layers/systems.
 * In REST APIs, DTOs define the shape of request/response JSON.
 * 
 * Why not use domain Post directly?
 * 
 *   1. SECURITY: Control what fields are exposed
 *      - Request: only title, content (not id, timestamps)
 *      - Response: all fields except sensitive data
 *   
 *   2. VALIDATION: Add validation rules
 *      - @NotBlank, @Size, etc.
 *      - Domain shouldn't know about HTTP validation
 *   
 *   3. DECOUPLING: API contract separate from domain
 *      - Can change domain without changing API
 *      - Can version APIs independently
 * 
 * =============================================================================
 * JAVA RECORD
 * =============================================================================
 * 
 * This is a Java "record" (Java 14+), not a regular class.
 * 
 * Records are:
 *   - Immutable (fields are final)
 *   - Auto-generate: constructor, getters, equals, hashCode, toString
 *   - Perfect for DTOs!
 * 
 * This record is equivalent to:
 * 
 *   public class CreatePostRequest {
 *       private final String title;
 *       private final String content;
 *       
 *       public CreatePostRequest(String title, String content) {
 *           this.title = title;
 *           this.content = content;
 *       }
 *       
 *       public String title() { return title; }
 *       public String content() { return content; }
 *       
 *       // equals(), hashCode(), toString() auto-generated
 *   }
 * 
 * =============================================================================
 * VALIDATION ANNOTATIONS
 * =============================================================================
 * 
 * Validation annotations are from Jakarta Bean Validation (JSR-380).
 * Spring validates automatically when @Valid is used in controller.
 * 
 * If validation fails:
 *   - Spring returns HTTP 400 Bad Request
 *   - Response includes which fields failed and why
 */
public record CreatePostRequest(
        
        /**
         * Post title.
         * 
         * @NotBlank = must not be null AND must contain at least one non-whitespace character
         *   - null → fails
         *   - "" → fails
         *   - "   " → fails (only whitespace)
         *   - "Hello" → passes
         * 
         * @Size = controls min/max length
         *   - min = 1 (at least 1 character)
         *   - max = 255 (matches database column)
         * 
         * message = custom error message shown when validation fails
         */
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,
        
        /**
         * Post content.
         * 
         * @NotBlank = required, can't be empty
         * 
         * No @Size limit because content is stored as TEXT (unlimited).
         */
        @NotBlank(message = "Content is required")
        String content
        
) {}

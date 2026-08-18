package com.vineet.posts.infrastructure.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing post.
 * 
 * Same structure as CreatePostRequest, but kept separate because:
 *   1. Update might have different validation rules in future
 *   2. Update might allow partial updates (PATCH)
 *   3. Explicit is better than implicit
 * 
 * For now, it's identical to CreatePostRequest - that's OK!
 * It's better to have clear intent than to over-optimize.
 */
public record UpdatePostRequest(
        
        /**
         * New title for the post.
         */
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,
        
        /**
         * New content for the post.
         */
        @NotBlank(message = "Content is required")
        String content
        
) {}

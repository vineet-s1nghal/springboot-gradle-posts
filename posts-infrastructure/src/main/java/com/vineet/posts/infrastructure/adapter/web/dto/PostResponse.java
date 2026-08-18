package com.vineet.posts.infrastructure.adapter.web.dto;

import com.vineet.posts.domain.Post;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Post data.
 * 
 * This is what the API returns to clients.
 * 
 * =============================================================================
 * WHY A RESPONSE DTO?
 * =============================================================================
 * 
 * We could return the domain Post directly, but:
 * 
 *   1. CONTROL: We decide exactly what fields to expose
 *      - Maybe we don't want to expose certain fields
 *      - Maybe we want to add computed fields
 *   
 *   2. STABILITY: API contract stays stable
 *      - If domain Post changes, we can keep response the same
 *      - Clients don't break when internal model evolves
 *   
 *   3. SERIALIZATION: Better control over JSON output
 *      - Can add @JsonFormat for date formatting
 *      - Can rename fields with @JsonProperty
 * 
 * =============================================================================
 * FACTORY METHOD PATTERN
 * =============================================================================
 * 
 * The from(Post) method converts domain to DTO.
 * 
 * Why a factory method?
 *   - Clear intent: PostResponse.from(post)
 *   - Keeps conversion logic with the DTO
 *   - Easy to find: "How do I create PostResponse?" → look at from()
 */
public record PostResponse(
        
        /**
         * Unique identifier.
         */
        UUID id,
        
        /**
         * Post title.
         */
        String title,
        
        /**
         * Post content.
         */
        String content,
        
        /**
         * When the post was created.
         */
        LocalDateTime createdAt,
        
        /**
         * When the post was last updated.
         */
        LocalDateTime updatedAt
        
) {
    
    /**
     * Factory method to create PostResponse from domain Post.
     * 
     * Usage:
     *   Post post = ...;
     *   PostResponse response = PostResponse.from(post);
     * 
     * @param post The domain post entity
     * @return PostResponse DTO ready for JSON serialization
     */
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

package com.vineet.posts.application.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested post is not found.
 * 
 * =============================================================================
 * WHY A CUSTOM EXCEPTION?
 * =============================================================================
 * 
 * 1. MORE MEANINGFUL than generic exceptions
 *    
 *    Bad:  throw new RuntimeException("Not found");
 *    Good: throw new PostNotFoundException(id);
 *    
 * 2. CARRIES CONTEXT - includes the ID that wasn't found
 *    
 *    catch (PostNotFoundException e) {
 *        log.warn("Post {} not found", e.getPostId());
 *    }
 *    
 * 3. CATCHABLE SEPARATELY from other exceptions
 *    
 *    try { ... }
 *    catch (PostNotFoundException e) { return 404; }
 *    catch (Exception e) { return 500; }
 *    
 * 4. INFRASTRUCTURE CAN CONVERT to HTTP response
 *    
 *    PostNotFoundException → HTTP 404 Not Found
 *    
 * =============================================================================
 * CHECKED vs UNCHECKED EXCEPTIONS
 * =============================================================================
 * 
 * CHECKED (extends Exception):
 *   - Compiler FORCES you to handle or declare
 *   - Good for recoverable errors
 *   - Example: IOException, SQLException
 *   
 *   void readFile() throws IOException { ... }  // Must declare!
 *   
 * UNCHECKED (extends RuntimeException) - OUR CHOICE:
 *   - Compiler doesn't force handling
 *   - Good for programming errors or unrecoverable situations
 *   - Example: NullPointerException, IllegalArgumentException
 *   
 *   void getPost() { throw new PostNotFoundException(id); }  // No declaration needed
 *   
 * We use UNCHECKED because:
 *   - "Post not found" is a business situation, not a programming error
 *   - We don't want every method to declare "throws PostNotFoundException"
 *   - The controller will catch and convert to HTTP 404
 */
public class PostNotFoundException extends RuntimeException {
    
    /**
     * Serial version UID for serialization.
     * Required because RuntimeException is Serializable.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The ID of the post that was not found.
     * Stored for logging, debugging, and error responses.
     */
    private final UUID postId;
    
    /**
     * Create exception with the post ID that wasn't found.
     * 
     * @param postId The UUID that was searched for
     */
    public PostNotFoundException(UUID postId) {
        super("Post not found with id: " + postId);
        this.postId = postId;
    }
    
    /**
     * Get the ID of the post that wasn't found.
     * 
     * Useful for:
     * - Logging: log.warn("Post not found: {}", e.getPostId());
     * - Error response: {"error": "Post not found", "id": "..."}
     * 
     * @return The post UUID that was not found
     */
    public UUID getPostId() {
        return postId;
    }
}

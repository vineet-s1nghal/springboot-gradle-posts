package com.vineet.posts.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Post domain entity.
 * 
 * =============================================================================
 * WHAT IS A DOMAIN ENTITY?
 * =============================================================================
 * 
 * A domain entity represents a core business concept in your application.
 * In our case, a "Post" is something users create, read, update, and delete.
 * 
 * This class is PURE JAVA:
 * - No Spring annotations (@Component, @Service, etc.)
 * - No JPA annotations (@Entity, @Column, etc.)
 * - No framework dependencies
 * 
 * WHY?
 * - Can be tested without any framework
 * - Can be reused if you change frameworks
 * - Focuses only on business logic
 * 
 * =============================================================================
 * DESIGN DECISIONS
 * =============================================================================
 * 
 * 1. UUID instead of Long for ID:
 *    - Globally unique (can generate without database)
 *    - Doesn't reveal how many records exist (security)
 *    - Works well in distributed systems
 * 
 * 2. Private constructor + Factory methods:
 *    - Controls how objects are created
 *    - Different methods for different scenarios (create vs load from DB)
 *    - More readable: Post.create() vs new Post(null, title, content, null, null)
 * 
 * 3. No setters:
 *    - All changes go through business methods
 *    - Business rules are enforced (e.g., updatedAt auto-updates)
 *    - Object is always in a valid state
 */
public class Post {
    
    // ==========================================================================
    // FIELDS
    // ==========================================================================
    
    /**
     * Unique identifier for the post.
     * Generated when post is created, never changes.
     */
    private UUID id;
    
    /**
     * Title of the post.
     * Required, max 255 characters (enforced in infrastructure layer).
     */
    private String title;
    
    /**
     * Content/body of the post.
     * Can be long text (stored as TEXT in database).
     */
    private String content;
    
    /**
     * When the post was created.
     * Set once during creation, never changes.
     */
    private LocalDateTime createdAt;
    
    /**
     * When the post was last updated.
     * Updated automatically when content changes.
     */
    private LocalDateTime updatedAt;
    
    // ==========================================================================
    // CONSTRUCTORS
    // ==========================================================================
    
    /**
     * Private constructor - use factory methods instead.
     * 
     * Why private?
     * - Forces use of create() or reconstitute()
     * - Ensures objects are created correctly
     * - Makes code more readable
     */
    private Post() {
        // Empty - fields set by factory methods
    }
    
    // ==========================================================================
    // FACTORY METHODS
    // ==========================================================================
    
    /**
     * Creates a NEW post.
     * 
     * Use this when a user creates a new post.
     * - Generates new UUID
     * - Sets timestamps to now
     * 
     * Example:
     *   Post post = Post.create("My Title", "My content...");
     * 
     * @param title   The post title (required)
     * @param content The post content (required)
     * @return A new Post instance with generated ID and timestamps
     */
    public static Post create(String title, String content) {
        Post post = new Post();
        post.id = UUID.randomUUID();
        post.title = title;
        post.content = content;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = LocalDateTime.now();
        return post;
    }
    
    /**
     * Reconstitutes a Post from persistence (database).
     * 
     * Use this when loading an existing post from the database.
     * - Uses existing ID (doesn't generate new one)
     * - Uses existing timestamps (doesn't set to now)
     * 
     * Why separate from create()?
     * - create() is for NEW posts (generate ID, set timestamps)
     * - reconstitute() is for EXISTING posts (use stored values)
     * 
     * Example:
     *   Post post = Post.reconstitute(
     *       existingId, 
     *       "Title from DB", 
     *       "Content from DB",
     *       createdAtFromDB,
     *       updatedAtFromDB
     *   );
     * 
     * @param id        The existing post ID
     * @param title     The post title
     * @param content   The post content
     * @param createdAt When the post was created
     * @param updatedAt When the post was last updated
     * @return A Post instance with the provided values
     */
    public static Post reconstitute(UUID id, String title, String content,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        Post post = new Post();
        post.id = id;
        post.title = title;
        post.content = content;
        post.createdAt = createdAt;
        post.updatedAt = updatedAt;
        return post;
    }
    
    // ==========================================================================
    // BUSINESS METHODS
    // ==========================================================================
    
    /**
     * Updates the post with new title and content.
     * 
     * This is a BUSINESS METHOD - it encapsulates the business rule:
     * "When a post is updated, the updatedAt timestamp must change"
     * 
     * Why not just setTitle() and setContent()?
     * - Setters don't know about business rules
     * - Someone might forget to update the timestamp
     * - This method ensures the rule is ALWAYS applied
     * 
     * Example:
     *   post.update("New Title", "New content...");
     *   // updatedAt is automatically set to now
     * 
     * @param title   The new title
     * @param content The new content
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ==========================================================================
    // GETTERS (No setters - changes through business methods only)
    // ==========================================================================
    
    public UUID getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // ==========================================================================
    // OBJECT METHODS
    // ==========================================================================
    
    /**
     * Two posts are equal if they have the same ID.
     * 
     * Why only ID?
     * - ID uniquely identifies a post
     * - Two posts with same ID are the "same" post, even if content differs
     * - This is called "Entity equality" (vs "Value equality")
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * String representation for debugging.
     */
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + (content != null && content.length() > 50 
                    ? content.substring(0, 50) + "..." 
                    : content) + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

package com.vineet.posts.application.port;

import com.vineet.posts.domain.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) for Post persistence operations.
 * 
 * =============================================================================
 * WHAT IS A PORT?
 * =============================================================================
 * 
 * In Hexagonal Architecture, a PORT is an interface that defines
 * what the application needs from the outside world.
 * 
 * This is an OUTBOUND PORT (also called "driven port"):
 * - The application USES this port to persist data
 * - The infrastructure layer IMPLEMENTS this port
 * 
 * Think of it as a "plug" - the application defines the shape,
 * and the infrastructure provides a matching "socket".
 * 
 * =============================================================================
 * WHY AN INTERFACE?
 * =============================================================================
 * 
 * 1. DECOUPLING: Application doesn't know about JPA, PostgreSQL, or Hibernate
 *    
 *    Application layer:  "I need to save a Post"
 *    Infrastructure:     "I'll handle it with JPA + PostgreSQL"
 *    
 * 2. TESTABILITY: Can easily mock for unit tests
 *    
 *    In tests: PostRepository mock = Mockito.mock(PostRepository.class);
 *    
 * 3. FLEXIBILITY: Can swap implementations
 *    
 *    Today:    JpaPostRepository (PostgreSQL)
 *    Tomorrow: MongoPostRepository (MongoDB)
 *    
 *    Application layer code doesn't change!
 * 
 * =============================================================================
 * NAMING CONVENTION
 * =============================================================================
 * 
 * - We call it "Repository" (not "DAO") following Domain-Driven Design
 * - Repository = collection-like interface for domain objects
 * - DAO = data access object (more database-focused naming)
 */
public interface PostRepository {
    
    /**
     * Save a post (create or update).
     * 
     * If the post doesn't exist in database → INSERT
     * If the post already exists → UPDATE
     * 
     * @param post The post to save
     * @return The saved post (may have updated fields)
     */
    Post save(Post post);
    
    /**
     * Find a post by its ID.
     * 
     * Returns Optional because the post might not exist.
     * This forces the caller to handle the "not found" case.
     * 
     * Why Optional instead of null?
     * - Explicit: caller MUST handle missing case
     * - No NullPointerException surprises
     * - More readable code with .orElse(), .orElseThrow()
     * 
     * @param id The post UUID
     * @return Optional containing the post if found, empty otherwise
     */
    Optional<Post> findById(UUID id);
    
    /**
     * Find all posts.
     * 
     * Returns empty list (not null) if no posts exist.
     * 
     * Note: In production with many posts, you'd want pagination:
     *   Page<Post> findAll(Pageable pageable);
     * 
     * @return List of all posts (may be empty, never null)
     */
    List<Post> findAll();
    
    /**
     * Delete a post by its ID.
     * 
     * If post doesn't exist, this method does nothing (idempotent).
     * 
     * @param id The post UUID to delete
     */
    void deleteById(UUID id);
    
    /**
     * Check if a post exists.
     * 
     * More efficient than findById() when you only need to check existence.
     * Database can use: SELECT COUNT(*) > 0 or SELECT EXISTS(...)
     * 
     * @param id The post UUID
     * @return true if exists, false otherwise
     */
    boolean existsById(UUID id);
}

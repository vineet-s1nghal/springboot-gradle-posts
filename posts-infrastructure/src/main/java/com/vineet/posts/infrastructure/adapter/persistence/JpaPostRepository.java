package com.vineet.posts.infrastructure.adapter.persistence;

import com.vineet.posts.application.port.PostRepository;
import com.vineet.posts.domain.Post;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of the PostRepository port.
 * 
 * =============================================================================
 * WHAT IS AN ADAPTER?
 * =============================================================================
 * 
 * In Hexagonal Architecture, an ADAPTER connects the application to the outside world.
 * 
 * This adapter:
 *   - IMPLEMENTS the PostRepository interface (defined in application layer)
 *   - USES Spring Data JPA (SpringDataPostRepository) for actual database work
 *   - CONVERTS between domain Post and JPA PostJpaEntity
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                          APPLICATION LAYER                              │
 * │                                                                         │
 * │   Use Cases ───────► PostRepository (interface)                         │
 * │                              │                                          │
 * │                              │ "I need save(), findById(), etc."        │
 * └──────────────────────────────┼──────────────────────────────────────────┘
 *                                │
 *                                │ implements
 *                                ▼
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                       INFRASTRUCTURE LAYER                              │
 * │                                                                         │
 * │   JpaPostRepository (this class) ◄─── ADAPTER                          │
 * │         │                                                               │
 * │         │ uses                                                          │
 * │         ▼                                                               │
 * │   SpringDataPostRepository ───────► PostgreSQL                          │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * =============================================================================
 * WHY THIS ADAPTER LAYER?
 * =============================================================================
 * 
 * Q: Why not just use SpringDataPostRepository directly in use cases?
 * 
 * A: Because:
 *    1. SpringDataPostRepository works with PostJpaEntity (JPA entity)
 *    2. Use cases work with Post (domain entity)
 *    3. This adapter translates between them!
 * 
 *    Also:
 *    - Use cases don't depend on Spring Data
 *    - We could swap Spring Data for something else (JDBC, MongoDB)
 *    - Only this file would change, not the use cases
 */
@Component  // Marks this as a Spring bean - Spring will create and manage it
public class JpaPostRepository implements PostRepository {
    
    /**
     * The Spring Data repository that does the actual database work.
     */
    private final SpringDataPostRepository springDataRepository;
    
    /**
     * Constructor injection.
     * 
     * Spring automatically injects SpringDataPostRepository
     * (which Spring Data auto-generates for us).
     */
    public JpaPostRepository(SpringDataPostRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    
    // =========================================================================
    // PostRepository INTERFACE IMPLEMENTATION
    // =========================================================================
    
    /**
     * Save a post (create or update).
     * 
     * FLOW:
     *   1. Convert domain Post → JPA PostJpaEntity
     *   2. Save using Spring Data (INSERT or UPDATE)
     *   3. Convert saved PostJpaEntity → domain Post
     *   4. Return domain Post
     */
    @Override
    public Post save(Post post) {
        // Convert domain → JPA entity
        PostJpaEntity jpaEntity = toJpaEntity(post);
        
        // Save (Spring Data handles INSERT vs UPDATE automatically)
        PostJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        
        // Convert back to domain
        return toDomain(savedEntity);
    }
    
    /**
     * Find a post by ID.
     * 
     * FLOW:
     *   1. Call Spring Data findById (returns Optional<PostJpaEntity>)
     *   2. If found, convert PostJpaEntity → Post
     *   3. Return Optional<Post>
     */
    @Override
    public Optional<Post> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);  // Convert if present
    }
    
    /**
     * Find all posts.
     * 
     * FLOW:
     *   1. Call Spring Data findAll (returns List<PostJpaEntity>)
     *   2. Convert each PostJpaEntity → Post
     *   3. Return List<Post>
     */
    @Override
    public List<Post> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a post by ID.
     */
    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
    
    /**
     * Check if a post exists.
     */
    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id);
    }
    
    // =========================================================================
    // MAPPING METHODS (Domain ↔ JPA Entity)
    // =========================================================================
    
    /**
     * Convert domain Post to JPA entity.
     * 
     * Used when SAVING to database.
     * 
     * Post (domain) → PostJpaEntity (JPA)
     */
    private PostJpaEntity toJpaEntity(Post post) {
        return new PostJpaEntity(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
    
    /**
     * Convert JPA entity to domain Post.
     * 
     * Used when LOADING from database.
     * 
     * PostJpaEntity (JPA) → Post (domain)
     * 
     * NOTE: We use Post.reconstitute() because we're loading EXISTING data,
     * not creating a new post (which would use Post.create()).
     */
    private Post toDomain(PostJpaEntity entity) {
        return Post.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

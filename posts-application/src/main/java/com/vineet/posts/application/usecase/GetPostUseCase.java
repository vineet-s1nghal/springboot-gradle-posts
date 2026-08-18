package com.vineet.posts.application.usecase;

import com.vineet.posts.application.exception.PostNotFoundException;
import com.vineet.posts.application.port.PostRepository;
import com.vineet.posts.domain.Post;

import java.util.List;
import java.util.UUID;

/**
 * Use case for retrieving posts.
 * 
 * Handles both:
 *   - Getting a single post by ID
 *   - Getting all posts
 * 
 * WHY BOTH IN ONE CLASS?
 * 
 * They're closely related "read" operations.
 * Some teams prefer separate classes:
 *   - GetPostByIdUseCase
 *   - GetAllPostsUseCase
 * 
 * Either approach is fine. We combine them here for simplicity.
 */
public class GetPostUseCase {
    
    private final PostRepository postRepository;
    
    /**
     * Constructor injection of repository.
     * 
     * @param postRepository The repository for post persistence
     */
    public GetPostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    /**
     * Get a single post by ID.
     * 
     * FLOW:
     *   1. Ask repository to find the post
     *   2. If found → return it
     *   3. If not found → throw PostNotFoundException
     * 
     * WHY THROW EXCEPTION INSTEAD OF RETURNING NULL?
     * 
     *   Returning null:
     *     Post post = getPostUseCase.execute(id);
     *     if (post == null) { ... }  // Easy to forget this check!
     *     post.getTitle();           // NullPointerException!
     *   
     *   Throwing exception:
     *     Post post = getPostUseCase.execute(id);  // Throws if not found
     *     post.getTitle();  // Safe - we know it exists
     *   
     *   The controller will catch the exception and return HTTP 404.
     * 
     * @param id The post UUID
     * @return The post
     * @throws PostNotFoundException if post doesn't exist
     */
    public Post execute(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    
    /**
     * Get all posts.
     * 
     * Returns empty list (not null) if no posts exist.
     * 
     * NOTE: In a real application with many posts, you'd want pagination:
     * 
     *   public Page<Post> execute(int page, int size) {
     *       return postRepository.findAll(PageRequest.of(page, size));
     *   }
     * 
     * For learning, we keep it simple with no pagination.
     * 
     * @return List of all posts (may be empty)
     */
    public List<Post> executeAll() {
        return postRepository.findAll();
    }
}

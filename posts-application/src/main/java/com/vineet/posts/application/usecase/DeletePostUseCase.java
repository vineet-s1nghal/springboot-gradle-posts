package com.vineet.posts.application.usecase;

import com.vineet.posts.application.exception.PostNotFoundException;
import com.vineet.posts.application.port.PostRepository;

import java.util.UUID;

/**
 * Use case for deleting a post.
 * 
 * DESIGN DECISION: Verify existence before deleting
 * 
 * There are two approaches to delete:
 * 
 * Approach 1: Just delete, don't check (idempotent)
 *   postRepository.deleteById(id);  // Does nothing if not exists
 *   // Returns success even if post didn't exist
 *   
 * Approach 2: Check first, then delete (our choice)
 *   if (!postRepository.existsById(id)) {
 *       throw new PostNotFoundException(id);
 *   }
 *   postRepository.deleteById(id);
 *   // Returns 404 if post didn't exist
 * 
 * We chose Approach 2 because:
 *   - User should know if they tried to delete something that doesn't exist
 *   - Helps catch bugs (e.g., wrong ID passed)
 *   - Consistent behavior with update (which also checks existence)
 */
public class DeletePostUseCase {
    
    private final PostRepository postRepository;
    
    /**
     * Constructor injection of repository.
     * 
     * @param postRepository The repository for post persistence
     */
    public DeletePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    /**
     * Execute the use case: delete a post.
     * 
     * FLOW:
     *   1. Check if post exists
     *   2. If not → throw PostNotFoundException
     *   3. If yes → delete it
     * 
     * WHY void RETURN TYPE?
     * 
     * After deletion, there's nothing meaningful to return.
     * Options:
     *   - void (our choice) - simple, clear intent
     *   - boolean - true if deleted, false if not found (but we throw instead)
     *   - Post - return the deleted post (but why? it's gone!)
     * 
     * The controller will return HTTP 204 No Content on success.
     * 
     * @param id The post UUID to delete
     * @throws PostNotFoundException if post doesn't exist
     */
    public void execute(UUID id) {
        // Step 1: Verify post exists
        // existsById is more efficient than findById when we don't need the data
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(id);
        }
        
        // Step 2: Delete
        postRepository.deleteById(id);
    }
}

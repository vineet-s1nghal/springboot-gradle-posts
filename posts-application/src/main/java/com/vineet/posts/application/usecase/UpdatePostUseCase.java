package com.vineet.posts.application.usecase;

import com.vineet.posts.application.exception.PostNotFoundException;
import com.vineet.posts.application.port.PostRepository;
import com.vineet.posts.domain.Post;

import java.util.UUID;

/**
 * Use case for updating an existing post.
 * 
 * FLOW:
 *   1. Find the existing post
 *   2. If not found → throw exception
 *   3. Update the post using domain method
 *   4. Save the updated post
 *   5. Return the updated post
 */
public class UpdatePostUseCase {
    
    private final PostRepository postRepository;
    
    /**
     * Constructor injection of repository.
     * 
     * @param postRepository The repository for post persistence
     */
    public UpdatePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    /**
     * Execute the use case: update an existing post.
     * 
     * IMPORTANT: We first LOAD the post, then UPDATE it.
     * 
     * Why not just save with the new data?
     * 
     *   BAD approach:
     *     Post post = Post.reconstitute(id, newTitle, newContent, ???, ???);
     *     // Problem: We don't know the original createdAt!
     *     // Problem: We'd lose data we didn't want to change!
     *   
     *   GOOD approach (what we do):
     *     Post post = repository.findById(id);     // Load existing
     *     post.update(newTitle, newContent);       // Modify
     *     repository.save(post);                   // Save changes
     *     
     *     // createdAt is preserved!
     *     // updatedAt is auto-set by domain method!
     * 
     * @param id      The post UUID to update
     * @param title   The new title
     * @param content The new content
     * @return The updated post
     * @throws PostNotFoundException if post doesn't exist
     */
    public Post execute(UUID id, String title, String content) {
        // Step 1: Find existing post (or throw)
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        
        // Step 2: Update using domain method
        // This encapsulates the business rule: updatedAt changes automatically
        post.update(title, content);
        
        // Step 3: Save and return
        return postRepository.save(post);
    }
}

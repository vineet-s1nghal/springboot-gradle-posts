package com.vineet.posts.application.usecase;

import com.vineet.posts.application.port.PostRepository;
import com.vineet.posts.domain.Post;

/**
 * Use case for creating a new post.
 * 
 * =============================================================================
 * WHAT IS A USE CASE?
 * =============================================================================
 * 
 * A Use Case represents a single action/operation that a user can perform.
 * It contains the business logic for that specific operation.
 * 
 * Use Case = "What can the user do?"
 *   - Create a post
 *   - View a post
 *   - Update a post
 *   - Delete a post
 * 
 * =============================================================================
 * WHY SEPARATE USE CASE CLASSES?
 * =============================================================================
 * 
 * Instead of one big PostService with all methods:
 * 
 *   class PostService {
 *       createPost() { }
 *       getPost() { }
 *       updatePost() { }
 *       deletePost() { }
 *       // ... becomes huge over time
 *   }
 * 
 * We have focused classes:
 * 
 *   class CreatePostUseCase { execute() { } }   ← You are here
 *   class GetPostUseCase { execute() { } }
 *   class UpdatePostUseCase { execute() { } }
 *   class DeletePostUseCase { execute() { } }
 * 
 * Benefits:
 *   - Single Responsibility: one class, one job
 *   - Easy to find: "Where's create logic?" → CreatePostUseCase
 *   - Easy to test: test one use case at a time
 *   - Easy to modify: change one without affecting others
 *   - Open/Closed: add new features = new use case class
 * 
 * =============================================================================
 * NO SPRING ANNOTATIONS!
 * =============================================================================
 * 
 * Notice there's no @Service or @Component here.
 * This class is pure Java - no framework dependency.
 * 
 * Spring will create this bean in the infrastructure layer
 * using a @Configuration class (we'll create that later).
 */
public class CreatePostUseCase {
    
    /**
     * Repository for persisting posts.
     * 
     * This is the PORT (interface) - we don't know the implementation.
     * Could be JPA, MongoDB, in-memory, etc.
     */
    private final PostRepository postRepository;
    
    /**
     * Constructor injection.
     * 
     * WHY CONSTRUCTOR INJECTION (not field injection)?
     * 
     * Field injection (@Autowired on field):
     *   @Autowired
     *   private PostRepository postRepository;  // BAD
     *   
     *   Problems:
     *   - Can't create object without Spring
     *   - Dependencies are hidden
     *   - Hard to test
     * 
     * Constructor injection (our approach):
     *   public CreatePostUseCase(PostRepository postRepository) {
     *       this.postRepository = postRepository;
     *   }
     *   
     *   Benefits:
     *   - Dependencies are explicit (visible in constructor)
     *   - Can create object without Spring (for testing)
     *   - Fields can be final (immutable)
     *   - Fails fast if dependency is missing
     * 
     * @param postRepository The repository for post persistence
     */
    public CreatePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    /**
     * Execute the use case: create a new post.
     * 
     * FLOW:
     *   1. Create domain entity using factory method
     *   2. Save to repository
     *   3. Return saved post
     * 
     * The Post.create() method handles:
     *   - Generating UUID
     *   - Setting createdAt and updatedAt to now
     * 
     * @param title   The post title
     * @param content The post content
     * @return The created post with generated ID and timestamps
     */
    public Post execute(String title, String content) {
        // Step 1: Create domain entity
        // Post.create() generates ID and sets timestamps
        Post post = Post.create(title, content);
        
        // Step 2: Save and return
        // Repository handles the actual persistence
        return postRepository.save(post);
    }
}

package com.vineet.posts.infrastructure.config;

import com.vineet.posts.application.port.PostRepository;
import com.vineet.posts.application.usecase.CreatePostUseCase;
import com.vineet.posts.application.usecase.DeletePostUseCase;
import com.vineet.posts.application.usecase.GetPostUseCase;
import com.vineet.posts.application.usecase.UpdatePostUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for use case beans.
 * 
 * =============================================================================
 * WHY DO WE NEED THIS?
 * =============================================================================
 * 
 * Our use cases are plain Java classes - no Spring annotations (@Service, @Component).
 * This is intentional - the application layer shouldn't depend on Spring!
 * 
 * But Spring needs to know how to create these objects for dependency injection.
 * This configuration class tells Spring: "Here's how to create each use case"
 * 
 * =============================================================================
 * HOW IT WORKS
 * =============================================================================
 * 
 * @Configuration - Marks this class as a source of bean definitions
 * @Bean - Marks a method that creates a bean
 * 
 * At startup, Spring:
 *   1. Finds this @Configuration class
 *   2. Calls each @Bean method
 *   3. Stores the returned objects in the application context
 *   4. Injects them wherever needed
 * 
 * =============================================================================
 * DEPENDENCY INJECTION CHAIN
 * =============================================================================
 * 
 *   PostRepository (interface)
 *         │
 *         │ implemented by
 *         ▼
 *   JpaPostRepository (@Component - auto-detected)
 *         │
 *         │ injected into
 *         ▼
 *   CreatePostUseCase, GetPostUseCase, etc. (@Bean - defined here)
 *         │
 *         │ injected into
 *         ▼
 *   PostController (@RestController - auto-detected)
 */
@Configuration
public class UseCaseConfig {
    
    /**
     * Creates the CreatePostUseCase bean.
     * 
     * Spring will:
     *   1. See that this method needs a PostRepository
     *   2. Find JpaPostRepository (implements PostRepository)
     *   3. Inject it into this method
     *   4. Call new CreatePostUseCase(postRepository)
     *   5. Store the result as a bean
     * 
     * @param postRepository Automatically injected by Spring
     * @return CreatePostUseCase bean
     */
    @Bean
    public CreatePostUseCase createPostUseCase(PostRepository postRepository) {
        return new CreatePostUseCase(postRepository);
    }
    
    /**
     * Creates the GetPostUseCase bean.
     */
    @Bean
    public GetPostUseCase getPostUseCase(PostRepository postRepository) {
        return new GetPostUseCase(postRepository);
    }
    
    /**
     * Creates the UpdatePostUseCase bean.
     */
    @Bean
    public UpdatePostUseCase updatePostUseCase(PostRepository postRepository) {
        return new UpdatePostUseCase(postRepository);
    }
    
    /**
     * Creates the DeletePostUseCase bean.
     */
    @Bean
    public DeletePostUseCase deletePostUseCase(PostRepository postRepository) {
        return new DeletePostUseCase(postRepository);
    }
}

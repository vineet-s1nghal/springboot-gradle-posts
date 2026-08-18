package com.vineet.posts.infrastructure.adapter.web;

import com.vineet.posts.application.usecase.CreatePostUseCase;
import com.vineet.posts.application.usecase.DeletePostUseCase;
import com.vineet.posts.application.usecase.GetPostUseCase;
import com.vineet.posts.application.usecase.UpdatePostUseCase;
import com.vineet.posts.domain.Post;
import com.vineet.posts.infrastructure.adapter.web.dto.CreatePostRequest;
import com.vineet.posts.infrastructure.adapter.web.dto.PostResponse;
import com.vineet.posts.infrastructure.adapter.web.dto.UpdatePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Post operations.
 * 
 * =============================================================================
 * SPRING MVC ANNOTATIONS
 * =============================================================================
 * 
 * @RestController = @Controller + @ResponseBody
 *   - @Controller: Handles HTTP requests
 *   - @ResponseBody: Return value is written directly to response (as JSON)
 * 
 * @RequestMapping("/api/posts")
 *   - Base path for all endpoints in this controller
 *   - All methods will be under /api/posts/...
 * 
 * =============================================================================
 * HTTP METHODS → CRUD OPERATIONS
 * =============================================================================
 * 
 * POST   /api/posts       → Create (C)
 * GET    /api/posts       → Read all (R)
 * GET    /api/posts/{id}  → Read one (R)
 * PUT    /api/posts/{id}  → Update (U)
 * DELETE /api/posts/{id}  → Delete (D)
 * 
 * =============================================================================
 * SWAGGER/OPENAPI ANNOTATIONS
 * =============================================================================
 * 
 * @Tag: Groups endpoints in Swagger UI
 * @Operation: Describes what the endpoint does
 * @ApiResponses: Documents possible responses
 * @Parameter: Describes a parameter
 * 
 * These generate interactive documentation at /swagger-ui.html
 */
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management APIs")
public class PostController {
    
    private final CreatePostUseCase createPostUseCase;
    private final GetPostUseCase getPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    
    /**
     * Constructor injection of all use cases.
     * 
     * Spring automatically injects the use case beans.
     * We'll configure these beans in the next phase.
     */
    public PostController(
            CreatePostUseCase createPostUseCase,
            GetPostUseCase getPostUseCase,
            UpdatePostUseCase updatePostUseCase,
            DeletePostUseCase deletePostUseCase) {
        this.createPostUseCase = createPostUseCase;
        this.getPostUseCase = getPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
    }
    
    // =========================================================================
    // CREATE
    // =========================================================================
    
    /**
     * Create a new post.
     * 
     * POST /api/posts
     * 
     * Request body:
     *   {
     *     "title": "My Post Title",
     *     "content": "Post content here..."
     *   }
     * 
     * Response: 201 Created
     *   {
     *     "id": "550e8400-e29b-41d4-a716-446655440000",
     *     "title": "My Post Title",
     *     "content": "Post content here...",
     *     "createdAt": "2024-01-15T10:30:00",
     *     "updatedAt": "2024-01-15T10:30:00"
     *   }
     * 
     * @Valid triggers validation on the request body.
     * If validation fails, Spring returns 400 Bad Request automatically.
     */
    @PostMapping
    @Operation(summary = "Create a new post", description = "Creates a new post with the provided title and content")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request) {
        
        // Execute use case
        Post post = createPostUseCase.execute(request.title(), request.content());
        
        // Convert to response DTO and return with 201 Created
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PostResponse.from(post));
    }
    
    // =========================================================================
    // READ (All)
    // =========================================================================
    
    /**
     * Get all posts.
     * 
     * GET /api/posts
     * 
     * Response: 200 OK
     *   [
     *     { "id": "...", "title": "...", ... },
     *     { "id": "...", "title": "...", ... }
     *   ]
     */
    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieves a list of all posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        
        List<PostResponse> posts = getPostUseCase.executeAll()
                .stream()
                .map(PostResponse::from)
                .toList();
        
        return ResponseEntity.ok(posts);
    }
    
    // =========================================================================
    // READ (One)
    // =========================================================================
    
    /**
     * Get a single post by ID.
     * 
     * GET /api/posts/{id}
     * 
     * Response: 200 OK (if found)
     *   { "id": "...", "title": "...", ... }
     * 
     * Response: 404 Not Found (if not found)
     *   { "error": "Post not found", ... }
     * 
     * @PathVariable extracts {id} from the URL path.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves a single post by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post found",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostResponse> getPostById(
            @Parameter(description = "Post UUID") @PathVariable UUID id) {
        
        Post post = getPostUseCase.execute(id);
        
        return ResponseEntity.ok(PostResponse.from(post));
    }
    
    // =========================================================================
    // UPDATE
    // =========================================================================
    
    /**
     * Update an existing post.
     * 
     * PUT /api/posts/{id}
     * 
     * Request body:
     *   {
     *     "title": "Updated Title",
     *     "content": "Updated content..."
     *   }
     * 
     * Response: 200 OK (if found and updated)
     * Response: 404 Not Found (if not found)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Updates an existing post with new title and content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "Post UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequest request) {
        
        Post post = updatePostUseCase.execute(id, request.title(), request.content());
        
        return ResponseEntity.ok(PostResponse.from(post));
    }
    
    // =========================================================================
    // DELETE
    // =========================================================================
    
    /**
     * Delete a post.
     * 
     * DELETE /api/posts/{id}
     * 
     * Response: 204 No Content (if deleted successfully)
     * Response: 404 Not Found (if not found)
     * 
     * 204 No Content means:
     *   - Request was successful
     *   - No response body needed
     *   - Resource has been deleted
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post", description = "Deletes a post by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "Post UUID") @PathVariable UUID id) {
        
        deletePostUseCase.execute(id);
        
        return ResponseEntity.noContent().build();
    }
}

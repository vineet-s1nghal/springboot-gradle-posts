package com.vineet.posts.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for PostJpaEntity.
 * 
 * =============================================================================
 * WHAT IS SPRING DATA JPA?
 * =============================================================================
 * 
 * Spring Data JPA is MAGIC! You define an interface, Spring provides the implementation.
 * 
 * Traditional approach (without Spring Data):
 * 
 *   public class PostDaoImpl implements PostDao {
 *       public Post save(Post post) {
 *           entityManager.persist(post);
 *           return post;
 *       }
 *       public Optional<Post> findById(UUID id) {
 *           return Optional.ofNullable(entityManager.find(Post.class, id));
 *       }
 *       public List<Post> findAll() {
 *           return entityManager.createQuery("SELECT p FROM Post p", Post.class)
 *                              .getResultList();
 *       }
 *       // ... lots of boilerplate code!
 *   }
 * 
 * Spring Data approach (what we do):
 * 
 *   public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID> {
 *       // That's it! Spring generates all the code automatically!
 *   }
 * 
 * =============================================================================
 * HOW IT WORKS
 * =============================================================================
 * 
 * JpaRepository<EntityType, IdType>
 *                │           │
 *                │           └── Type of the primary key (UUID)
 *                └── Entity class this repository manages (PostJpaEntity)
 * 
 * By extending JpaRepository, we get these methods FOR FREE:
 * 
 *   CRUD Operations:
 *   - save(entity)          → INSERT or UPDATE
 *   - findById(id)          → SELECT * WHERE id = ?
 *   - findAll()             → SELECT *
 *   - deleteById(id)        → DELETE WHERE id = ?
 *   - existsById(id)        → SELECT EXISTS(...)
 *   - count()               → SELECT COUNT(*)
 * 
 *   Batch Operations:
 *   - saveAll(entities)     → Bulk insert/update
 *   - deleteAll()           → Delete everything
 *   - findAllById(ids)      → SELECT * WHERE id IN (?, ?, ?)
 * 
 * =============================================================================
 * CUSTOM QUERIES
 * =============================================================================
 * 
 * We can add custom queries using method naming conventions:
 * 
 *   // Find by title (exact match)
 *   List<PostJpaEntity> findByTitle(String title);
 *   // Generated: SELECT * FROM posts WHERE title = ?
 * 
 *   // Find by title containing (LIKE)
 *   List<PostJpaEntity> findByTitleContaining(String keyword);
 *   // Generated: SELECT * FROM posts WHERE title LIKE '%keyword%'
 * 
 *   // Find by creation date after
 *   List<PostJpaEntity> findByCreatedAtAfter(LocalDateTime date);
 *   // Generated: SELECT * FROM posts WHERE created_at > ?
 * 
 * For now, we only need the basic CRUD methods, so the interface is empty!
 */
@Repository
public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID> {
    
    // All basic CRUD operations are inherited from JpaRepository!
    // We can add custom query methods here later if needed.
    
    // Examples of custom queries we might add:
    // List<PostJpaEntity> findByTitleContainingIgnoreCase(String keyword);
    // List<PostJpaEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    // Optional<PostJpaEntity> findByTitleAndContent(String title, String content);
}

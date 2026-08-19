# Phase 1: Complete Concepts Reference Guide

> **Purpose:** This document explains all Java, OOP, Spring Boot, and architectural concepts used in Phase 1 of the Posts API project. Refer to this whenever you need to understand how something works.

---

## Table of Contents

1. [Project Structure & Architecture](#1-project-structure--architecture)
2. [Phase 1.4: Domain Layer Concepts](#2-phase-14-domain-layer-concepts)
3. [Phase 1.5: Application Layer Concepts](#3-phase-15-application-layer-concepts)
4. [Phase 1.6: Infrastructure - Persistence Concepts](#4-phase-16-infrastructure---persistence-concepts)
5. [Phase 1.7: Infrastructure - Web Adapter Concepts](#5-phase-17-infrastructure---web-adapter-concepts)
6. [Phase 1.8: Exception Handling & Configuration Concepts](#6-phase-18-exception-handling--configuration-concepts)
7. [Complete Request Flow](#7-complete-request-flow)

---

## 1. Project Structure & Architecture

### 1.1 What is Hexagonal Architecture?

Hexagonal Architecture (also called "Ports & Adapters") organizes code into layers that separate **business logic** from **external systems**.

```
┌─────────────────────────────────────────────────────────────────┐
│                    YOUR APPLICATION                              │
│                                                                  │
│   ┌──────────────────┐                  ┌──────────────────┐    │
│   │   WEB BROWSER    │                  │    DATABASE      │    │
│   │   (User clicks)  │                  │   (PostgreSQL)   │    │
│   └────────┬─────────┘                  └────────▲─────────┘    │
│            │                                     │               │
│            ▼                                     │               │
│   ┌────────────────────────────────────────────────────────┐    │
│   │              INFRASTRUCTURE LAYER                       │    │
│   │         (Spring Boot, JPA, Controllers)                 │    │
│   │         "The doors and windows of your house"           │    │
│   └────────────────────────┬───────────────────────────────┘    │
│                            │                                     │
│                            ▼                                     │
│   ┌────────────────────────────────────────────────────────┐    │
│   │              APPLICATION LAYER                          │    │
│   │           (Use Cases - what can you DO?)                │    │
│   │         "The rooms and their purposes"                  │    │
│   └────────────────────────┬───────────────────────────────┘    │
│                            │                                     │
│                            ▼                                     │
│   ┌────────────────────────────────────────────────────────┐    │
│   │                 DOMAIN LAYER                            │    │
│   │            (Core business objects - Post)               │    │
│   │            "The foundation of your house"               │    │
│   └────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Why Three Separate Modules?

| Layer              | What it contains            | Can change without affecting   |
| ------------------ | --------------------------- | ------------------------------ |
| **Domain**         | What is a "Post"?           | Nothing - it's the core        |
| **Application**    | What can you DO with Posts? | Domain stays same              |
| **Infrastructure** | HOW to save/retrieve Posts  | Domain & Application stay same |

**Real-world example:** If you switch from PostgreSQL to MongoDB:

- Domain layer: NO changes
- Application layer: NO changes
- Infrastructure layer: Only change the persistence adapter

### 1.3 Project Folder Structure

```
springboot-gradle-posts/
├── build.gradle                 # Root build configuration
├── settings.gradle              # Defines which modules exist
├── gradle.properties            # Build settings
├── docker-compose.yml           # PostgreSQL container
│
├── posts-domain/                # DOMAIN LAYER
│   ├── build.gradle
│   └── src/main/java/com/vineet/posts/domain/
│       └── Post.java            # Core business entity
│
├── posts-application/           # APPLICATION LAYER
│   ├── build.gradle
│   └── src/main/java/com/vineet/posts/application/
│       ├── port/
│       │   └── PostRepository.java      # Interface (port)
│       ├── usecase/
│       │   ├── CreatePostUseCase.java
│       │   ├── GetPostUseCase.java
│       │   ├── UpdatePostUseCase.java
│       │   └── DeletePostUseCase.java
│       └── exception/
│           └── PostNotFoundException.java
│
└── posts-infrastructure/        # INFRASTRUCTURE LAYER
    ├── build.gradle
    └── src/main/java/com/vineet/posts/infrastructure/
        ├── PostsApplication.java        # Spring Boot main class
        ├── adapter/
        │   ├── persistence/             # Database adapter
        │   │   ├── PostJpaEntity.java
        │   │   ├── SpringDataPostRepository.java
        │   │   └── JpaPostRepository.java
        │   └── web/                     # Web adapter
        │       ├── PostController.java
        │       ├── dto/
        │       │   ├── CreatePostRequest.java
        │       │   ├── UpdatePostRequest.java
        │       │   ├── PostResponse.java
        │       │   └── ErrorResponse.java
        │       └── exception/
        │           └── GlobalExceptionHandler.java
        └── config/
            ├── UseCaseConfig.java
            └── OpenApiConfig.java
```

### 1.4 Module Dependencies

```
posts-infrastructure
        │
        │ depends on
        ▼
posts-application
        │
        │ depends on
        ▼
posts-domain (no dependencies - pure Java)
```

**Key Rule:** Dependencies only flow DOWNWARD. Domain never depends on Application or Infrastructure.

---

## 2. Phase 1.4: Domain Layer Concepts

The domain layer contains your **core business object** - pure Java with no framework dependencies.

### 2.1 What is a Class?

A **class** is a blueprint/template for creating objects.

```
┌─────────────────────────────────────────┐
│           POST FORM (Class)             │
├─────────────────────────────────────────┤
│  ID:        _______________             │
│  Title:     _______________             │
│  Content:   _______________             │
│  Created:   _______________             │
│  Updated:   _______________             │
└─────────────────────────────────────────┘
```

When you fill out the form, you create an **object** (instance):

```
┌─────────────────────────────────────────┐
│          FILLED POST (Object)           │
├─────────────────────────────────────────┤
│  ID:        abc-123-def                 │
│  Title:     My First Post               │
│  Content:   Hello World!                │
│  Created:   2026-08-18 10:00            │
│  Updated:   2026-08-18 10:00            │
└─────────────────────────────────────────┘
```

### 2.2 Fields (Instance Variables)

Fields are variables that belong to an object. They store the object's data.

```java
public class Post {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

| Field       | Type            | What it stores                                                |
| ----------- | --------------- | ------------------------------------------------------------- |
| `id`        | `UUID`          | Unique identifier like `550e8400-e29b-41d4-a716-446655440000` |
| `title`     | `String`        | Text like `"My First Post"`                                   |
| `content`   | `String`        | Longer text                                                   |
| `createdAt` | `LocalDateTime` | Date and time like `2026-08-18T10:30:00`                      |
| `updatedAt` | `LocalDateTime` | Date and time                                                 |

### 2.3 Access Modifiers

Access modifiers control who can access fields and methods.

| Modifier    | Who can access              |
| ----------- | --------------------------- |
| `public`    | Anyone, anywhere            |
| `private`   | Only code inside THIS class |
| `protected` | This class + subclasses     |
| (none)      | Classes in the same package |

```java
private String title;  // Only Post class can directly access this
```

**Why `private`?**

- Protects data from being changed incorrectly
- Forces use of methods (like getters) to access data
- Called "encapsulation" - hiding internal details

### 2.4 Constructors

A **constructor** is a special method that creates new objects.

```java
// Constructor - same name as class, no return type
public Post(String title, String content) {
    this.title = title;
    this.content = content;
}

// Usage:
Post post = new Post("Hello", "World");
```

**`this` keyword:** Refers to the current object. Used to distinguish between field and parameter with same name.

```java
public Post(String title) {
    this.title = title;  // this.title = field, title = parameter
}
```

### 2.5 Private Constructor + Factory Methods

Instead of public constructors, we use **factory methods** - static methods that create objects.

```java
public class Post {
    // Private constructor - can't use "new Post()" from outside
    private Post() { }

    // Factory method for NEW posts
    public static Post create(String title, String content) {
        Post post = new Post();
        post.id = UUID.randomUUID();      // Generate new ID
        post.title = title;
        post.content = content;
        post.createdAt = LocalDateTime.now();  // Set to NOW
        post.updatedAt = LocalDateTime.now();
        return post;
    }

    // Factory method for EXISTING posts (from database)
    public static Post reconstitute(UUID id, String title, String content,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        Post post = new Post();
        post.id = id;                     // Use EXISTING ID
        post.title = title;
        post.content = content;
        post.createdAt = createdAt;       // Use EXISTING timestamp
        post.updatedAt = updatedAt;
        return post;
    }
}
```

**Why factory methods?**

```java
// Without factory methods - confusing!
Post post = new Post(null, "Title", "Content", null, null);

// With factory methods - clear intent!
Post newPost = Post.create("Title", "Content");           // NEW post
Post existingPost = Post.reconstitute(id, "Title", ...);  // FROM database
```

### 2.6 Static Methods

A **static** method belongs to the CLASS, not to an object.

```java
// Static method - called on the CLASS
Post post = Post.create("Title", "Content");  // Post.create()

// Non-static method - called on an OBJECT
post.update("New Title", "New Content");      // post.update()
```

| Type       | How to call          | Uses object data? |
| ---------- | -------------------- | ----------------- |
| Static     | `ClassName.method()` | No                |
| Non-static | `object.method()`    | Yes               |

### 2.7 Getters and Setters

**Getters** let you READ field values. **Setters** let you WRITE field values.

```java
// Getter - returns the field value
public String getTitle() {
    return title;
}

// Setter - changes the field value
public void setTitle(String title) {
    this.title = title;
}
```

**In our Post class, we have getters but NO setters.** Why?

- All changes go through the `update()` method
- This ensures business rules are always followed
- The object is always in a valid state

### 2.8 Business Methods

A **business method** contains business logic - rules about how your application works.

```java
public void update(String title, String content) {
    this.title = title;
    this.content = content;
    this.updatedAt = LocalDateTime.now();  // Business rule: auto-update timestamp!
}
```

**Business Rule:** "When a post is updated, the `updatedAt` timestamp must change automatically."

If we had simple setters instead:

```java
// Someone might forget to update the timestamp!
post.setTitle("New Title");
post.setContent("New Content");
// Oops, forgot: post.setUpdatedAt(LocalDateTime.now());
```

### 2.9 equals() and hashCode()

These methods define how objects are compared.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;                          // Same object?
    if (o == null || getClass() != o.getClass()) return false;  // Same type?
    Post post = (Post) o;
    return Objects.equals(id, post.id);                  // Same ID?
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

**`@Override`:** Tells Java "I'm replacing a method from the parent class." The parent (Object) has default equals/hashCode that we're customizing.

**Why compare by ID only?**

```
Post A: id=123, title="Hello"
Post B: id=123, title="Hello Updated"

Are they the same post? YES! Same ID = same post.
The title changed, but it's still the same post.
```

### 2.10 toString()

Returns a string representation of the object (useful for debugging).

```java
@Override
public String toString() {
    return "Post{id=" + id + ", title='" + title + "'}";
}

// Usage:
System.out.println(post);  // Prints: Post{id=abc-123, title='Hello'}
```

### 2.11 UUID (Universally Unique Identifier)

A UUID is a 128-bit identifier that's practically guaranteed to be unique.

```java
UUID id = UUID.randomUUID();
// Example: 550e8400-e29b-41d4-a716-446655440000
```

**Why UUID instead of numbers (1, 2, 3)?**

- Can generate without database (no need to ask DB for next ID)
- Globally unique (even across different databases)
- Doesn't reveal how many records exist (security)

### 2.12 LocalDateTime

Represents date AND time without timezone.

```java
LocalDateTime now = LocalDateTime.now();  // 2026-08-18T10:30:00
```

---

## 3. Phase 1.5: Application Layer Concepts

The application layer defines **what actions users can perform** (use cases) and **what capabilities are needed** (ports/interfaces).

### 3.1 What is an Interface?

An **interface** is a contract that defines WHAT methods must exist, but not HOW they work.

```java
public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(UUID id);
    List<Post> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
```

Think of it like a job description:

```
┌─────────────────────────────────────────────────────────────┐
│           JOB: POST REPOSITORY                              │
│                                                             │
│   Must be able to:                                          │
│   ☐ Save a post                                             │
│   ☐ Find a post by ID                                       │
│   ☐ Find all posts                                          │
│   ☐ Delete a post by ID                                     │
│   ☐ Check if a post exists                                  │
│                                                             │
│   HOW you do it = your choice                               │
│   (PostgreSQL, MongoDB, file, memory... doesn't matter)     │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Interface vs Class

| Aspect          | Interface                   | Class                    |
| --------------- | --------------------------- | ------------------------ |
| Contains        | Method signatures (no code) | Method signatures + code |
| Purpose         | Defines WHAT                | Defines WHAT + HOW       |
| Create objects? | No                          | Yes                      |
| Keyword         | `interface`                 | `class`                  |

```java
// Interface - WHAT (no implementation)
public interface PostRepository {
    Post save(Post post);  // No code, just signature
}

// Class - HOW (has implementation)
public class JpaPostRepository implements PostRepository {
    @Override
    public Post save(Post post) {
        // Actual code that saves to database
        return springDataRepository.save(toJpaEntity(post));
    }
}
```

### 3.3 Why Use Interfaces? (Abstraction)

```
WITHOUT INTERFACE:
┌─────────────────────────────────────────────────────────────┐
│   CreatePostUseCase → JpaPostRepository → PostgreSQL        │
│                                                             │
│   Problem: If we want MongoDB, we must change UseCase code! │
└─────────────────────────────────────────────────────────────┘

WITH INTERFACE:
┌─────────────────────────────────────────────────────────────┐
│   CreatePostUseCase → PostRepository (interface)            │
│                              ↑                              │
│                              │ implements                   │
│                    ┌─────────┴─────────┐                    │
│                    │                   │                    │
│             JpaPostRepository    MongoPostRepository        │
│                    ↓                   ↓                    │
│               PostgreSQL           MongoDB                  │
│                                                             │
│   UseCase doesn't care which one is used!                   │
└─────────────────────────────────────────────────────────────┘
```

### 3.4 What is `implements`?

When a class **implements** an interface, it promises to provide code for all the interface's methods.

```java
// Interface defines WHAT
public interface PostRepository {
    Post save(Post post);
}

// Class implements HOW
public class JpaPostRepository implements PostRepository {
    @Override
    public Post save(Post post) {
        // Actual implementation here
    }
}
```

### 3.5 What is Optional<T>?

`Optional` is a container that may or may not contain a value. It's Java's way of handling "maybe not found" cases.

```java
Optional<Post> findById(UUID id);
```

```
findById("abc-123")

If post exists:     Optional[Post{id=abc-123, ...}]
If post NOT exists: Optional.empty
```

**Why not just return `null`?**

```java
// BAD: Returning null
Post post = repository.findById(id);  // Returns null if not found
post.getTitle();  // 💥 NullPointerException if post is null!

// GOOD: Using Optional
Optional<Post> optionalPost = repository.findById(id);

// Method 1: Check if present
if (optionalPost.isPresent()) {
    Post post = optionalPost.get();
    post.getTitle();  // Safe!
}

// Method 2: Provide default
Post post = optionalPost.orElse(defaultPost);

// Method 3: Throw if not found (what we use)
Post post = optionalPost.orElseThrow(() -> new PostNotFoundException(id));
```

### 3.6 What is List<T>?

`List` is a collection that holds multiple items in order.

```java
List<Post> findAll();  // Returns a list of Post objects
```

```
List<Post> posts = findAll();

posts = [ Post1, Post2, Post3, Post4 ]
          ↓      ↓      ↓      ↓
        index   index  index  index
          0       1      2      3

posts.get(0)    →  Post1
posts.size()    →  4
posts.isEmpty() →  false
```

### 3.7 Generics (<T>)

Generics let you write code that works with different types.

```java
List<Post>    // A list that contains Post objects
List<String>  // A list that contains String objects
Optional<Post>  // An Optional that may contain a Post
```

The `<T>` is a placeholder for the actual type:

- `List<Post>` means "List of Post"
- `Optional<UUID>` means "Optional UUID"

### 3.8 What is a Use Case?

A **Use Case** is a single action a user can perform.

```
┌─────────────────────────────────────────────────────────────┐
│                    USER ACTIONS                              │
│                                                              │
│   "I want to create a post"    →  CreatePostUseCase          │
│   "I want to read posts"       →  GetPostUseCase             │
│   "I want to update a post"    →  UpdatePostUseCase          │
│   "I want to delete a post"    →  DeletePostUseCase          │
└─────────────────────────────────────────────────────────────┘
```

Each Use Case class has:

1. A **repository** (to access data)
2. An **execute()** method (to perform the action)

### 3.9 Constructor Injection (Dependency Injection)

**Dependency:** Something your class NEEDS to work.

**Injection:** Instead of creating the dependency yourself, you RECEIVE it from outside.

```java
public class CreatePostUseCase {
    private final PostRepository postRepository;  // Dependency

    // Constructor receives the dependency (injection)
    public CreatePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
}
```

**Why injection instead of creating inside?**

```java
// BAD: Creating dependency inside (hard-coded)
public class CreatePostUseCase {
    private PostRepository repo = new JpaPostRepository();  // Stuck with JPA!
}

// GOOD: Receiving dependency from outside (injected)
public class CreatePostUseCase {
    private final PostRepository repo;

    public CreatePostUseCase(PostRepository repo) {  // Can receive ANY implementation!
        this.repo = repo;
    }
}
```

### 3.10 The `final` Keyword

`final` means "cannot be changed after assignment."

```java
private final PostRepository postRepository;
```

- Once set in constructor, it stays the same forever
- Safer - no one can accidentally replace it later
- Makes the class more predictable

### 3.11 Lambda Expressions (Arrow Functions)

A **lambda** is a short way to write a function.

```java
// Full anonymous class:
optionalPost.orElseThrow(new Supplier<PostNotFoundException>() {
    @Override
    public PostNotFoundException get() {
        return new PostNotFoundException(id);
    }
});

// Lambda (same thing, shorter):
optionalPost.orElseThrow(() -> new PostNotFoundException(id));
```

**Syntax:** `(parameters) -> expression`

```java
() -> new PostNotFoundException(id)   // No parameters
(x) -> x * 2                          // One parameter
(x, y) -> x + y                       // Two parameters
```

### 3.12 Method Chaining

Calling multiple methods in sequence, each returning an object for the next call.

```java
return postRepository.findById(id)              // Returns Optional<Post>
        .orElseThrow(() -> new PostNotFoundException(id));  // Returns Post or throws
```

Same as:

```java
Optional<Post> optionalPost = postRepository.findById(id);
Post post = optionalPost.orElseThrow(() -> new PostNotFoundException(id));
return post;
```

### 3.13 Custom Exceptions

A custom exception is an exception class you create for specific error situations.

```java
public class PostNotFoundException extends RuntimeException {
    private final UUID postId;

    public PostNotFoundException(UUID postId) {
        super("Post not found with id: " + postId);  // Message
        this.postId = postId;
    }

    public UUID getPostId() {
        return postId;
    }
}
```

### 3.14 extends (Inheritance)

`extends` means "this class is a type of" another class.

```java
public class PostNotFoundException extends RuntimeException
```

```
RuntimeException          (built-in Java exception)
       ↑
       │ extends
       │
PostNotFoundException    (our custom exception - IS A RuntimeException)
```

### 3.15 super() - Calling Parent Constructor

`super(...)` calls the parent class's constructor.

```java
public PostNotFoundException(UUID postId) {
    super("Post not found with id: " + postId);  // Calls RuntimeException(String message)
    this.postId = postId;
}
```

### 3.16 Checked vs Unchecked Exceptions

| Type      | Extends            | Compiler forces handling? | Use for                                 |
| --------- | ------------------ | ------------------------- | --------------------------------------- |
| Checked   | `Exception`        | Yes                       | Recoverable errors (file not found)     |
| Unchecked | `RuntimeException` | No                        | Programming errors, business exceptions |

We use **unchecked** (RuntimeException) because:

- Don't want every method to declare `throws PostNotFoundException`
- The controller will catch and convert to HTTP 404

---

## 4. Phase 1.6: Infrastructure - Persistence Concepts

The persistence adapter connects your application to the database using JPA (Java Persistence API).

### 4.1 What is JPA and Hibernate?

**JPA (Java Persistence API)** = A specification (set of rules) for how Java should talk to databases.

**Hibernate** = A library that implements JPA (does the actual work).

```
Your Code
    │
    │ uses
    ▼
JPA (Specification)  ←── "These are the rules"
    │
    │ implemented by
    ▼
Hibernate (Library)  ←── "I do the actual work"
    │
    │ talks to
    ▼
PostgreSQL (Database)
```

### 4.2 What is an Entity?

An **Entity** is a Java class that maps to a database table.

```java
@Entity
@Table(name = "posts")
public class PostJpaEntity {
    @Id
    private UUID id;

    @Column(name = "title")
    private String title;
    // ...
}
```

```
JAVA CLASS (PostJpaEntity)    ←→    DATABASE TABLE (posts)

class PostJpaEntity {                CREATE TABLE posts (
    UUID id;              ←→           id UUID,
    String title;         ←→           title VARCHAR(255),
    String content;       ←→           content TEXT,
    LocalDateTime createdAt; ←→        created_at TIMESTAMP,
    LocalDateTime updatedAt; ←→        updated_at TIMESTAMP
}                                    );
```

### 4.3 JPA Annotations

| Annotation                         | What it does                  | Example                              |
| ---------------------------------- | ----------------------------- | ------------------------------------ |
| `@Entity`                          | Marks class as database table | `@Entity`                            |
| `@Table(name="posts")`             | Specifies table name          | `@Table(name = "posts")`             |
| `@Id`                              | Marks primary key field       | `@Id`                                |
| `@Column(name="...")`              | Maps to column name           | `@Column(name = "title")`            |
| `@Column(nullable=false)`          | NOT NULL constraint           | `@Column(nullable = false)`          |
| `@Column(updatable=false)`         | Column never updates          | `@Column(updatable = false)`         |
| `@Column(length=255)`              | VARCHAR(255)                  | `@Column(length = 255)`              |
| `@Column(columnDefinition="TEXT")` | Use TEXT type                 | `@Column(columnDefinition = "TEXT")` |

### 4.4 Why Two Post Classes?

We have `Post` (domain) and `PostJpaEntity` (infrastructure).

| Aspect               | Post (Domain)  | PostJpaEntity (Infrastructure) |
| -------------------- | -------------- | ------------------------------ |
| Location             | posts-domain   | posts-infrastructure           |
| Annotations          | None           | @Entity, @Column, etc.         |
| Purpose              | Business logic | Database mapping               |
| Setters              | No             | Yes (JPA needs them)           |
| Framework dependency | None           | JPA/Hibernate                  |

**Why separate?**

- Domain stays pure Java (no framework dependencies)
- Can test domain without database
- If you switch from JPA to something else, domain doesn't change

### 4.5 What is Spring Data JPA?

Spring Data JPA is MAGIC! You define an interface, Spring creates the implementation.

```java
@Repository
public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID> {
    // That's it! Spring generates all the code automatically!
}
```

**Methods you get FOR FREE:**

| Method           | SQL Generated          |
| ---------------- | ---------------------- |
| `save(entity)`   | INSERT or UPDATE       |
| `findById(id)`   | SELECT \* WHERE id = ? |
| `findAll()`      | SELECT \*              |
| `deleteById(id)` | DELETE WHERE id = ?    |
| `existsById(id)` | SELECT EXISTS(...)     |
| `count()`        | SELECT COUNT(\*)       |

### 4.6 JpaRepository<Entity, IdType>

```java
public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID>
                                                               │              │
                                                               │              └── Type of primary key
                                                               └── Entity class this manages
```

### 4.7 Spring Annotations: @Component, @Repository, @Service

These annotations tell Spring to manage the class.

| Annotation    | Semantic meaning             | Use for          |
| ------------- | ---------------------------- | ---------------- |
| `@Component`  | General Spring-managed class | Any class        |
| `@Repository` | Data access class            | Database classes |
| `@Service`    | Business logic class         | Service layer    |
| `@Controller` | Web controller               | HTTP handlers    |

They all do the same thing technically, but convey different meanings.

### 4.8 What is a Spring Bean?

A **bean** is an object that Spring creates and manages.

When you add `@Component` (or `@Repository`, `@Service`, etc.), Spring:

1. Creates an instance of the class at startup
2. Stores it in the "application context"
3. Injects it wherever needed

```java
@Component  // "Spring, please create and manage this class"
public class JpaPostRepository {
    // Spring creates ONE instance and reuses it
}
```

### 4.9 The Adapter Pattern

An **adapter** converts between two incompatible interfaces - like a power adapter.

```
US Plug (110V)  ───►  ADAPTER  ───►  EU Socket (220V)

Same idea in code:

Application Layer        ADAPTER        Spring Data
(uses Post)       ───►  converts  ───►  (uses PostJpaEntity)
```

Our `JpaPostRepository`:

1. **Implements** `PostRepository` (what application layer expects)
2. **Uses** `SpringDataPostRepository` (what Spring Data provides)
3. **Converts** between `Post` and `PostJpaEntity`

### 4.10 Mapping Methods (toJpaEntity & toDomain)

```java
// Domain → JPA (when SAVING)
private PostJpaEntity toJpaEntity(Post post) {
    return new PostJpaEntity(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCreatedAt(),
        post.getUpdatedAt()
    );
}

// JPA → Domain (when LOADING)
private Post toDomain(PostJpaEntity entity) {
    return Post.reconstitute(
        entity.getId(),
        entity.getTitle(),
        entity.getContent(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
}
```

```
SAVING: Post ──toJpaEntity()──► PostJpaEntity ──► Database
LOADING: Post ◄──toDomain()─── PostJpaEntity ◄── Database
```

### 4.11 Stream API

Streams process collections in a functional style.

```java
return springDataRepository.findAll()    // List<PostJpaEntity>
        .stream()                         // Stream<PostJpaEntity>
        .map(this::toDomain)              // Stream<Post>
        .collect(Collectors.toList());    // List<Post>
```

| Method                          | What it does                               |
| ------------------------------- | ------------------------------------------ |
| `.stream()`                     | Convert list to stream                     |
| `.map(function)`                | Transform each element                     |
| `.filter(condition)`            | Keep elements matching condition           |
| `.collect(Collectors.toList())` | Convert stream back to list                |
| `.toList()`                     | Shorthand for collect(Collectors.toList()) |

### 4.12 Method References (::)

A **method reference** is shorthand for a simple lambda.

```java
// Lambda:
.map(entity -> this.toDomain(entity))

// Method reference (same thing):
.map(this::toDomain)
```

| Syntax              | Meaning                       |
| ------------------- | ----------------------------- |
| `this::method`      | Call method on current object |
| `ClassName::method` | Call static method            |
| `ClassName::new`    | Call constructor              |

### 4.13 Optional.map()

`map()` transforms the value inside an Optional (if present).

```java
return springDataRepository.findById(id)  // Optional<PostJpaEntity>
        .map(this::toDomain);              // Optional<Post>
```

```
CASE 1: Post found
Optional[PostJpaEntity] ──.map(toDomain)──► Optional[Post]

CASE 2: Post NOT found
Optional.empty ──.map(toDomain)──► Optional.empty (map does nothing)
```

---

## 5. Phase 1.7: Infrastructure - Web Adapter Concepts

The web adapter exposes HTTP endpoints (REST API) that clients can call.

### 5.1 What is a REST API?

**REST** = Representational State Transfer  
**API** = Application Programming Interface

A REST API lets different programs talk over HTTP.

```
CLIENT (Browser, Mobile App)
    │
    │  HTTP Request: GET /api/posts
    ▼
YOUR REST API
    │
    │  HTTP Response: [{"id": "...", "title": "..."}]
    ▼
CLIENT receives JSON data
```

### 5.2 HTTP Methods (Verbs)

| Method     | Purpose       | Example                 | Response       |
| ---------- | ------------- | ----------------------- | -------------- |
| **POST**   | Create new    | `POST /api/posts`       | 201 Created    |
| **GET**    | Read/retrieve | `GET /api/posts/123`    | 200 OK         |
| **PUT**    | Update entire | `PUT /api/posts/123`    | 200 OK         |
| **DELETE** | Delete        | `DELETE /api/posts/123` | 204 No Content |

**CRUD mapping:**

- **C**reate → POST
- **R**ead → GET
- **U**pdate → PUT
- **D**elete → DELETE

### 5.3 HTTP Status Codes

```
2xx = SUCCESS
─────────────
200 OK           → Request succeeded, here's the data
201 Created      → New resource was created
204 No Content   → Success, but nothing to return

4xx = CLIENT ERROR (your fault)
───────────────────────────────
400 Bad Request  → Invalid data sent (validation failed)
404 Not Found    → Resource doesn't exist

5xx = SERVER ERROR (our fault)
──────────────────────────────
500 Internal Server Error → Something broke on server
```

### 5.4 What is a DTO?

**DTO = Data Transfer Object**

A simple object that carries data between systems. In REST APIs, DTOs define the shape of request/response JSON.

```
CLIENT sends JSON:
{
  "title": "Hello",
  "content": "World"
}
    │
    │ Spring converts to
    ▼
CreatePostRequest DTO
    │
    │ Controller processes
    ▼
PostResponse DTO
    │
    │ Spring converts to
    ▼
CLIENT receives JSON:
{
  "id": "abc-123",
  "title": "Hello",
  "content": "World",
  "createdAt": "...",
  "updatedAt": "..."
}
```

**Why DTOs instead of domain objects?**

| Using Domain Directly            | Using DTOs               |
| -------------------------------- | ------------------------ |
| Client sees internal structure   | Control what client sees |
| Can't add validation annotations | Add @NotBlank, @Size     |
| Domain changes break API         | API stays stable         |

### 5.5 Java Records

A **record** is a special class type (Java 14+) perfect for DTOs.

```java
// What you write:
public record CreatePostRequest(String title, String content) {}

// What Java generates:
public class CreatePostRequest {
    private final String title;
    private final String content;

    public CreatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String title() { return title; }      // Note: title() not getTitle()
    public String content() { return content; }

    // equals(), hashCode(), toString() auto-generated
}
```

**Record getter naming:** `request.title()` not `request.getTitle()`

### 5.6 Validation Annotations

```java
public record CreatePostRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255)
    String title,

    @NotBlank(message = "Content is required")
    String content
) {}
```

| Annotation           | What it checks                           |
| -------------------- | ---------------------------------------- |
| `@NotBlank`          | Not null, not empty, not just whitespace |
| `@NotNull`           | Not null (but can be empty)              |
| `@NotEmpty`          | Not null and not empty                   |
| `@Size(min, max)`    | Length within range                      |
| `@Email`             | Valid email format                       |
| `@Min(n)`, `@Max(n)` | Number range                             |

### 5.7 @RestController

```java
@RestController
@RequestMapping("/api/posts")
public class PostController { }
```

| Annotation                      | Meaning                            |
| ------------------------------- | ---------------------------------- |
| `@RestController`               | Handle HTTP requests + return JSON |
| `@RequestMapping("/api/posts")` | Base URL for all methods           |

`@RestController` = `@Controller` + `@ResponseBody`

### 5.8 HTTP Method Annotations

```java
@PostMapping              // POST /api/posts
@GetMapping               // GET /api/posts
@GetMapping("/{id}")      // GET /api/posts/{id}
@PutMapping("/{id}")      // PUT /api/posts/{id}
@DeleteMapping("/{id}")   // DELETE /api/posts/{id}
```

### 5.9 @PathVariable

Extracts values from the URL path.

```java
@GetMapping("/{id}")
public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) { }
```

```
URL: /api/posts/550e8400-e29b-41d4-a716-446655440000
               └─────────────────┬─────────────────┘
                                 │
               @PathVariable UUID id = 550e8400-...
```

### 5.10 @RequestBody

Converts JSON request body to Java object.

```java
@PostMapping
public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request) { }
```

```
JSON: {"title": "Hello", "content": "World"}
                    │
                    │ @RequestBody converts
                    ▼
CreatePostRequest(title="Hello", content="World")
```

### 5.11 @Valid

Triggers validation on the annotated object.

```java
@PostMapping
public ResponseEntity<PostResponse> createPost(
    @Valid @RequestBody CreatePostRequest request  // @Valid triggers validation!
) { }
```

- Without `@Valid`: Validation annotations ignored
- With `@Valid`: Spring checks all validations before calling method
- If validation fails: Returns 400 Bad Request automatically

### 5.12 ResponseEntity

Gives full control over HTTP response (status code + body).

```java
// 200 OK with body
return ResponseEntity.ok(data);

// 201 Created with body
return ResponseEntity.status(HttpStatus.CREATED).body(data);

// 204 No Content (no body)
return ResponseEntity.noContent().build();

// 404 Not Found
return ResponseEntity.notFound().build();
```

### 5.13 Swagger/OpenAPI Annotations

Generate interactive documentation at `/swagger-ui.html`.

```java
@Operation(summary = "Create a new post")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(responseCode = "400", description = "Bad Request")
})
```

| Annotation                    | Purpose            |
| ----------------------------- | ------------------ |
| `@Tag(name = "Posts")`        | Group endpoints    |
| `@Operation(summary = "...")` | Describe endpoint  |
| `@ApiResponses`               | Document responses |
| `@Parameter`                  | Describe parameter |

---

## 6. Phase 1.8: Exception Handling & Configuration Concepts

Global exception handling and Spring configuration to wire everything together.

### 6.1 The Problem: Repetitive Error Handling

Without global handling, every method needs try-catch:

```java
// BAD: Same error handling code in every method
@GetMapping("/{id}")
public ResponseEntity<?> getPost(@PathVariable UUID id) {
    try {
        Post post = getPostUseCase.execute(id);
        return ResponseEntity.ok(PostResponse.from(post));
    } catch (PostNotFoundException e) {
        return ResponseEntity.status(404).body(errorResponse);  // Repeated!
    } catch (Exception e) {
        return ResponseEntity.status(500).body(errorResponse);  // Repeated!
    }
}
```

### 6.2 @RestControllerAdvice

Handles exceptions across ALL controllers in one place.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(...) { }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(...) { }
}
```

**How it works:**

```
Controller throws PostNotFoundException
        │
        │ Exception bubbles up
        ▼
Spring intercepts
        │
        │ Finds matching @ExceptionHandler
        ▼
GlobalExceptionHandler.handlePostNotFound()
        │
        │ Returns ResponseEntity
        ▼
Client receives HTTP 404 with error JSON
```

Now controllers are clean:

```java
// GOOD: No try-catch needed!
@GetMapping("/{id}")
public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
    Post post = getPostUseCase.execute(id);  // Let it throw!
    return ResponseEntity.ok(PostResponse.from(post));
}
```

### 6.3 @ExceptionHandler

Marks a method that handles a specific exception type.

```java
@ExceptionHandler(PostNotFoundException.class)
public ResponseEntity<ErrorResponse> handlePostNotFound(
        PostNotFoundException ex,      // The thrown exception
        HttpServletRequest request     // HTTP request info
) {
    // Create and return error response
}
```

**Order matters!** Spring matches the most specific exception first.

### 6.4 Consistent Error Response Format

```java
public record ErrorResponse(
    LocalDateTime timestamp,      // When error happened
    int status,                   // HTTP status (404, 400, 500)
    String error,                 // Short description
    String message,               // Detailed message
    String path,                  // URL that caused error
    List<FieldError> fieldErrors  // Validation errors (if any)
) { }
```

Example responses:

```json
// 404 Not Found
{
  "timestamp": "2026-08-19T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Post not found with id: abc-123",
  "path": "/api/posts/abc-123",
  "fieldErrors": null
}

// 400 Validation Error
{
  "timestamp": "2026-08-19T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Check 'fieldErrors' for details.",
  "path": "/api/posts",
  "fieldErrors": [
    {"field": "title", "message": "Title is required"}
  ]
}
```

### 6.5 Logger (SLF4J)

Writes messages for debugging and monitoring.

```java
private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

log.warn("Post not found: {}", postId);           // Warning
log.error("Unexpected error: {}", ex.getMessage(), ex);  // Error with stack trace
```

| Level         | When to use             |
| ------------- | ----------------------- |
| `log.trace()` | Very detailed debugging |
| `log.debug()` | Debug info              |
| `log.info()`  | Normal events           |
| `log.warn()`  | Potential problems      |
| `log.error()` | Errors                  |

**The `{}` placeholder:**

```java
log.warn("Post not found: {}", postId);  // {} replaced with postId value
```

### 6.6 Why Hide Error Details in 500?

```java
// Log full details (for developers)
log.error("Unexpected error: {}", ex.getMessage(), ex);

// Return generic message (for clients)
return ErrorResponse.of(500, "Internal Server Error",
    "An unexpected error occurred. Please try again later.", path);
```

**Security:** Don't reveal internal details to potential attackers.

```
BAD: "NullPointerException at UserService.java:42"
     (Reveals technology, file structure, vulnerabilities)

GOOD: "An unexpected error occurred. Please try again later."
      (Hackers learn nothing; developers check logs)
```

### 6.7 @Configuration

Marks a class as a source of bean definitions.

```java
@Configuration
public class UseCaseConfig {
    // Contains @Bean methods
}
```

### 6.8 @Bean

Creates a bean (Spring-managed object) from a method.

```java
@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePostUseCase createPostUseCase(PostRepository postRepository) {
        return new CreatePostUseCase(postRepository);
    }
}
```

**Translation:** "When someone needs a CreatePostUseCase, call this method to create one."

### 6.9 Why UseCaseConfig Exists

Use cases are plain Java (no @Component):

```java
// Application Layer - NO Spring annotations!
public class CreatePostUseCase {
    public CreatePostUseCase(PostRepository postRepository) { }
}
```

**Why no @Component?** Application layer shouldn't depend on Spring.

**Solution:** Infrastructure layer tells Spring how to create them:

```java
@Configuration
public class UseCaseConfig {
    @Bean
    public CreatePostUseCase createPostUseCase(PostRepository postRepository) {
        return new CreatePostUseCase(postRepository);
    }
}
```

### 6.10 Spring Dependency Injection Flow

```
SPRING STARTUP:

1. Scans for @Component, @Configuration, etc.

2. Finds:
   - JpaPostRepository (@Component)
   - UseCaseConfig (@Configuration)
   - PostController (@RestController)

3. Creates beans in dependency order:

   SpringDataPostRepository (auto by Spring Data)
           │
           ▼
   JpaPostRepository (needs SpringDataPostRepository)
           │
           ▼
   CreatePostUseCase, GetPostUseCase, etc. (need PostRepository)
           │
           ▼
   PostController (needs all use cases)

4. Application ready!
```

### 6.11 HttpServletRequest

Contains information about the incoming HTTP request.

```java
@ExceptionHandler(PostNotFoundException.class)
public ResponseEntity<ErrorResponse> handlePostNotFound(
        PostNotFoundException ex,
        HttpServletRequest request    // Spring injects automatically
) {
    String path = request.getRequestURI();  // "/api/posts/abc-123"
}
```

| Method                      | Returns                        |
| --------------------------- | ------------------------------ |
| `getRequestURI()`           | `/api/posts/abc-123`           |
| `getMethod()`               | `GET`, `POST`, `PUT`, `DELETE` |
| `getHeader("Content-Type")` | `application/json`             |

### 6.12 Nested Records

A record defined inside another record.

```java
public record ErrorResponse(
    // ... fields
    List<FieldError> fieldErrors
) {
    // Nested record
    public record FieldError(String field, String message) {}
}
```

Used as: `new ErrorResponse.FieldError("title", "required")`

---

## 7. Complete Request Flow

### 7.1 Create Post: Full Journey

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 1: Client sends HTTP request                                           │
│                                                                              │
│  POST /api/posts                                                             │
│  Content-Type: application/json                                              │
│  Body: { "title": "Hello", "content": "World" }                              │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 2: Spring routes to PostController.createPost()                        │
│                                                                              │
│  @PostMapping matches POST /api/posts                                        │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 3: Spring converts JSON → CreatePostRequest                            │
│                                                                              │
│  @RequestBody parses JSON                                                    │
│  CreatePostRequest(title="Hello", content="World")                           │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 4: @Valid triggers validation                                          │
│                                                                              │
│  Checks @NotBlank, @Size                                                     │
│  If fails → 400 Bad Request (method not called)                              │
│  If passes → continue                                                        │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 5: Controller calls Use Case                                           │
│                                                                              │
│  Post post = createPostUseCase.execute("Hello", "World");                    │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 6: Use Case creates domain Post                                        │
│                                                                              │
│  Post.create("Hello", "World")                                               │
│  → Generates UUID                                                            │
│  → Sets createdAt = now                                                      │
│  → Sets updatedAt = now                                                      │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 7: Use Case calls Repository                                           │
│                                                                              │
│  postRepository.save(post)                                                   │
│  (postRepository is actually JpaPostRepository)                              │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 8: JpaPostRepository converts and saves                                │
│                                                                              │
│  PostJpaEntity entity = toJpaEntity(post);  // Domain → JPA                  │
│  springDataRepository.save(entity);         // Actual DB save                │
│  return toDomain(savedEntity);              // JPA → Domain                  │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 9: SpringDataPostRepository executes SQL                               │
│                                                                              │
│  INSERT INTO posts (id, title, content, created_at, updated_at)              │
│  VALUES ('abc-123', 'Hello', 'World', '2026-08-19', '2026-08-19')            │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 10: Response travels back up                                           │
│                                                                              │
│  Database → SpringData → JpaPostRepository → UseCase → Controller            │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 11: Controller creates response                                        │
│                                                                              │
│  PostResponse.from(post)                                                     │
│  ResponseEntity.status(201).body(postResponse)                               │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  STEP 12: Spring converts to JSON                                            │
│                                                                              │
│  HTTP 201 Created                                                            │
│  {                                                                           │
│    "id": "abc-123-...",                                                      │
│    "title": "Hello",                                                         │
│    "content": "World",                                                       │
│    "createdAt": "2026-08-19T10:00:00",                                       │
│    "updatedAt": "2026-08-19T10:00:00"                                        │
│  }                                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 Error Flow (Post Not Found)

```
GET /api/posts/invalid-id
        │
        ▼
PostController.getPostById()
        │
        │ calls
        ▼
GetPostUseCase.execute(id)
        │
        │ calls
        ▼
postRepository.findById(id)
        │
        │ returns
        ▼
Optional.empty (not found)
        │
        │ triggers
        ▼
.orElseThrow(() -> new PostNotFoundException(id))
        │
        │ throws
        ▼
PostNotFoundException bubbles up
        │
        │ caught by
        ▼
GlobalExceptionHandler.handlePostNotFound()
        │
        │ returns
        ▼
HTTP 404 Not Found
{
  "status": 404,
  "error": "Not Found",
  "message": "Post not found with id: invalid-id"
}
```

---

## 8. Quick Reference Tables

### 8.1 Layer Responsibilities

| Layer          | Contains                        | Depends On          | Framework        |
| -------------- | ------------------------------- | ------------------- | ---------------- |
| Domain         | Post entity, business rules     | Nothing             | None (pure Java) |
| Application    | Use cases, Repository interface | Domain              | None (pure Java) |
| Infrastructure | Controllers, JPA, Config        | Application, Domain | Spring Boot, JPA |

### 8.2 Key Annotations Summary

| Annotation                    | Layer          | Purpose                   |
| ----------------------------- | -------------- | ------------------------- |
| `@Entity`                     | Infrastructure | Maps class to DB table    |
| `@Id`, `@Column`              | Infrastructure | Map fields to columns     |
| `@Repository`                 | Infrastructure | Spring-managed data class |
| `@Component`                  | Infrastructure | Spring-managed class      |
| `@RestController`             | Infrastructure | HTTP request handler      |
| `@RequestMapping`             | Infrastructure | URL path mapping          |
| `@GetMapping`, `@PostMapping` | Infrastructure | HTTP method mapping       |
| `@PathVariable`               | Infrastructure | Extract from URL          |
| `@RequestBody`                | Infrastructure | Parse JSON body           |
| `@Valid`                      | Infrastructure | Trigger validation        |
| `@NotBlank`, `@Size`          | Infrastructure | Validation rules          |
| `@Configuration`              | Infrastructure | Bean definition source    |
| `@Bean`                       | Infrastructure | Create Spring bean        |
| `@RestControllerAdvice`       | Infrastructure | Global exception handler  |
| `@ExceptionHandler`           | Infrastructure | Handle specific exception |

### 8.3 HTTP Status Codes Used

| Code | Meaning               | When Used                |
| ---- | --------------------- | ------------------------ |
| 200  | OK                    | GET success, PUT success |
| 201  | Created               | POST success             |
| 204  | No Content            | DELETE success           |
| 400  | Bad Request           | Validation failed        |
| 404  | Not Found             | Resource doesn't exist   |
| 500  | Internal Server Error | Unexpected error         |

### 8.4 API Endpoints

| Method | URL               | Description   | Success | Failure  |
| ------ | ----------------- | ------------- | ------- | -------- |
| POST   | `/api/posts`      | Create post   | 201     | 400      |
| GET    | `/api/posts`      | Get all posts | 200     | -        |
| GET    | `/api/posts/{id}` | Get one post  | 200     | 404      |
| PUT    | `/api/posts/{id}` | Update post   | 200     | 400, 404 |
| DELETE | `/api/posts/{id}` | Delete post   | 204     | 404      |

---

## 9. Glossary

| Term                       | Definition                                                   |
| -------------------------- | ------------------------------------------------------------ |
| **Bean**                   | Object created and managed by Spring                         |
| **DTO**                    | Data Transfer Object - carries data between layers           |
| **Entity**                 | Class that maps to a database table                          |
| **Factory Method**         | Static method that creates objects                           |
| **Getter**                 | Method that returns a field's value                          |
| **Hexagonal Architecture** | Architecture separating core logic from infrastructure       |
| **Interface**              | Contract defining method signatures without implementation   |
| **Lambda**                 | Short anonymous function: `() -> expression`                 |
| **Optional**               | Container that may or may not hold a value                   |
| **Port**                   | Interface defining what the application needs                |
| **Adapter**                | Class that implements a port, connecting to external systems |
| **Record**                 | Immutable class with auto-generated methods                  |
| **REST**                   | Architectural style for web APIs using HTTP methods          |
| **Stream**                 | Sequence of elements supporting functional operations        |
| **Use Case**               | Single user action/operation                                 |
| **UUID**                   | Universally Unique Identifier                                |

---

_Last updated: Phase 1 Complete_
_Next: Phase 2 - PDF Generation_

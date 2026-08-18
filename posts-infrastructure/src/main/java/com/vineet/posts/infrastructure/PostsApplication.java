package com.vineet.posts.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * 
 * This is the entry point of the application.
 * We'll add more configuration in Phase 1.8.
 * 
 * @SpringBootApplication combines:
 * - @Configuration: This class can define beans
 * - @EnableAutoConfiguration: Auto-configure based on dependencies
 * - @ComponentScan: Scan this package for components
 */
@SpringBootApplication
public class PostsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PostsApplication.class, args);
    }
}

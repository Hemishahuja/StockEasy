package com.example.stockeasy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.stockeasy.domain.Post;

/**
 * PostRepository interface for Post entity persistence operations.
 * Provides CRUD operations and custom queries for post management.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Find top 10 posts ordered by timestamp descending (most recent first)
     */
    @Query("SELECT p FROM Post p ORDER BY p.timestamp DESC")
    List<Post> findTop10ByOrderByTimeStampDesc();
}

package com.example.stockeasy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.Post;
import com.example.stockeasy.repo.PostRepository;

/**
 * PostService for post management operations.
 * Handles CRUD operations for community posts.
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * Get recent posts (latest first, limit 10)
     */
    public List<Post> getRecentPosts() {
        return postRepository.findTop10ByOrderByTimeStampDesc();
    }

    /**
     * Save a new post
     */
    public Post savePost(Post post) {
        return postRepository.save(post);
    }
}

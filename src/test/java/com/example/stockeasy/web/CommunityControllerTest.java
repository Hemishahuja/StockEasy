package com.example.stockeasy.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.stockeasy.domain.Post;
import com.example.stockeasy.service.PostService;
import com.example.stockeasy.service.UserService;

/**
 * CommunityController tests.
 * Tests community hub functionality including post display and creation.
 */
@WebMvcTest(CommunityController.class)
public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Test
    public void testGetCommunityPage()
            throws Exception {
        // Mock some posts
        Post post1 = new Post("TraderJoe", "AAPL looking strong today!");
        Post post2 = new Post("StockGuru", "TSLA earnings were amazing!");
        List<Post> posts = Arrays.asList(post1, post2);

        when(postService.getRecentPosts()).thenReturn(posts);

        mockMvc.perform(get("/community"))
                .andExpect(status().isOk())
                .andExpect(view().name("community/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasSize(2)))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", is(nullValue()))); // anonymous user

        verify(postService, times(1)).getRecentPosts();
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetCommunityPageWithAuthenticatedUser()
            throws Exception {
        // Mock authenticated user and posts
        Post post1 = new Post("TraderJoe", "AAPL looking strong today!");
        List<Post> posts = Arrays.asList(post1);

        when(postService.getRecentPosts()).thenReturn(posts);
        when(userService.loadUserByUsername("testuser")).thenReturn(
                new com.example.stockeasy.domain.User("testuser", "test@example.com", "password"));

        mockMvc.perform(get("/community"))
                .andExpect(status().isOk())
                .andExpect(view().name("community/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("username", equalTo("testuser"))));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostSuccess()
            throws Exception {
        // Mock user lookup
        com.example.stockeasy.domain.User user = new com.example.stockeasy.domain.User("testuser", "test@example.com", "password");
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        Post savedPost = new Post(1L, "testuser", "Test post content");
        when(postService.savePost(org.mockito.ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

        mockMvc.perform(post("/community")
                .param("content", "Test post content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/community"))
                .andExpect(flash().attributeExists("success"));

        verify(postService, times(1)).savePost(org.mockito.ArgumentMatchers.any(Post.class));
    }

    @Test
    public void testCreatePostUnauthenticated()
            throws Exception {
        mockMvc.perform(post("/community")
                .param("content", "Test post content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/community"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostValidationFail()
            throws Exception {
        // Empty content should fail validation
        com.example.stockeasy.domain.User user = new com.example.stockeasy.domain.User("testuser", "test@example.com", "password");
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        mockMvc.perform(post("/community")
                .param("content", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/community"))
                .andExpect(flash().attributeExists("error"));

        verify(postService, org.mockito.Mockito.never()).savePost(org.mockito.ArgumentMatchers.any(Post.class));
    }
}

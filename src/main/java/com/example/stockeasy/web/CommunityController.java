package com.example.stockeasy.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.stockeasy.domain.Post;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.PostService;
import com.example.stockeasy.service.UserService;

import jakarta.validation.Valid;

/**
 * CommunityController for community hub posts.
 * Handles post display, creation, and community interactions.
 */
@Controller
public class CommunityController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Display community hub with posts feed
     */
    @GetMapping("/community")
    public String getCommunity(Model model) {
        try {
            // Get current authenticated user if logged in
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
                String username = authentication.getName();
                User user = (User) userService.loadUserByUsername(username);
                model.addAttribute("user", user);
            } else {
                // Anonymous user
                model.addAttribute("user", null);
            }

            // Get recent posts
            List<Post> posts = postService.getRecentPosts();
            model.addAttribute("posts", posts);

        } catch (Exception e) {
            model.addAttribute("user", null);
            model.addAttribute("posts", List.of());
            model.addAttribute("error", "Failed to load community posts: " + e.getMessage());
        }

        return "community/index";
    }

    /**
     * Create a new community post
     */
    @PostMapping("/community")
    public String createPost(@Valid @RequestParam String content,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            // Check if user is authenticated
            if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
                redirectAttributes.addFlashAttribute("error", "You must be logged in to post.");
                return "redirect:/community";
            }

            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);

            // Create new post
            Post post = new Post(user.getId(), user.getUsername(), content);
            post.setTimestamp(LocalDateTime.now());

            // Save the post
            postService.savePost(post);

            redirectAttributes.addFlashAttribute("success", "Your post has been shared with the community!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create post. Please try again.");
        }

        return "redirect:/community";
    }
}

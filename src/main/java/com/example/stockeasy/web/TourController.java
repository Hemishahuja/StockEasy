package com.example.stockeasy.web;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockeasy.service.UserService;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class TourController {

    private final UserService userService;

    public TourController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/tour-complete")
    public ResponseEntity<?> markTourComplete(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"Not authenticated\"}");
        }

        try {
            // Mark tour as completed for this user
            String username = principal.getName();
            userService.setTourCompletedByUsername(username, true);
            return ResponseEntity.ok("{\"success\": true, \"message\": \"Tour completed\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to update tour status\"}");
        }
    }
}

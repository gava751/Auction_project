package com.auction.platform.controller;

import com.auction.platform.repository.UserRepository;
import com.auction.platform.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @PatchMapping("/users/{id}/block")
    public void blockUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> { u.setStatus("BANNED"); userRepository.save(u); });
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }
}
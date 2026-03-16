package com.auction.platform.controller;

import com.auction.platform.domain.User;
import com.auction.platform.repository.UserRepository;
import com.auction.platform.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.auction.platform.domain.SellerApplication;
import com.auction.platform.repository.SellerApplicationRepository;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SellerApplicationRepository applicationRepository;
    @PatchMapping("/users/{id}/block")
    public void blockUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> { u.setStatus("BANNED"); userRepository.save(u); });
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }

    @GetMapping("/applications")
    public List<SellerApplication> getPendingApplications() {
        return applicationRepository.findByStatus("PENDING");
    }
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<String> approveApplication(@PathVariable Long id) {
        SellerApplication app = applicationRepository.findById(id).orElseThrow();
        app.setStatus("APPROVED");
        applicationRepository.save(app);

        User user = app.getUser();
        user.setRole("ROLE_SELLER");
        userRepository.save(user);

        return ResponseEntity.ok("Заявка одобрена, пользователь стал продавцом!");
    }
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        SellerApplication app = applicationRepository.findById(id).orElseThrow();
        app.setStatus("REJECTED");
        applicationRepository.save(app);
        return ResponseEntity.ok("Заявка отклонена.");
    }

}
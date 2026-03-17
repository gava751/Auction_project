package com.auction.platform.controller;

import com.auction.platform.domain.Category;
import com.auction.platform.domain.User;
import com.auction.platform.repository.LotRepository;
import com.auction.platform.repository.UserRepository;
import com.auction.platform.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final LotRepository lotRepository;
    private final PasswordEncoder passwordEncoder;
    @PatchMapping("/users/{id}/block")
    public void blockUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> { u.setStatus("BANNED"); userRepository.save(u); });
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PatchMapping("/users/{id}/unblock")
    public void unblockUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setStatus("ACTIVE");
            userRepository.save(u);
        });
    }
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
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
    @GetMapping("/lots")
    public List<com.auction.platform.domain.Lot> getAllLots() {
        return lotRepository.findAll();
    }

    @DeleteMapping("/lots/{id}")
    public ResponseEntity<Void> adminDeleteLot(@PathVariable Long id) {
        lotRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        SellerApplication app = applicationRepository.findById(id).orElseThrow();
        app.setStatus("REJECTED");
        applicationRepository.save(app);
        return ResponseEntity.ok("Заявка отклонена.");
    }
    @PostMapping("/users/admin")
    public ResponseEntity<String> createNewAdmin(@RequestBody com.auction.platform.dto.AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Пользователь с таким Email уже существует");
        }
        User admin = new User();
        admin.setEmail(request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRole("ROLE_ADMIN");
        admin.setStatus("ACTIVE");
        admin.setFirstName("Системный");
        admin.setLastName("Администратор");
        userRepository.save(admin);
        return ResponseEntity.ok("Новый администратор успешно создан");
    }
}
package com.auction.platform.controller;

import com.auction.platform.domain.SellerApplication;
import com.auction.platform.domain.User;
import com.auction.platform.repository.SellerApplicationRepository;
import com.auction.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final SellerApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    public ResponseEntity<String> submitApplication(
            @RequestParam("passportData") String passportData,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (applicationRepository.existsByUserIdAndStatus(user.getId(), "PENDING")) {
            return ResponseEntity.badRequest().body("У вас уже есть заявка на рассмотрении");
        }

        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            SellerApplication app = new SellerApplication();
            app.setUser(user);
            app.setPassportData(passportData);
            app.setDocumentPath(filePath.toString());
            applicationRepository.save(app);

            return ResponseEntity.ok("Заявка успешно отправлена!");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки файла");
        }
    }
}
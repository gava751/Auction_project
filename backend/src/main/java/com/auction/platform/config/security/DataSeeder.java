package com.auction.platform.config.security;

import com.auction.platform.domain.Category;
import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.repository.CategoryRepository;
import com.auction.platform.repository.LotRepository;
import com.auction.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            LotRepository lotRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String email = "user@test.com";
            User buyer;

            if (!userRepository.existsByEmail(email)) {
                buyer = new User();
                buyer.setEmail(email);
                buyer.setPasswordHash(passwordEncoder.encode("1111"));
                buyer.setFirstName("Test");
                buyer.setLastName("User");
                buyer.setRole("ROLE_BUYER");
                buyer.setStatus("ACTIVE");
                buyer = userRepository.save(buyer);
            } else {
                buyer = userRepository.findByEmail(email).get();
                buyer.setPasswordHash(passwordEncoder.encode("1111"));
                buyer = userRepository.save(buyer);
            }
            if (!userRepository.existsByEmail("admin@test.com")) {
                User admin = new User();
                admin.setEmail("admin@test.com");
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                admin.setFirstName("Главный");
                admin.setLastName("Администратор");
                admin.setRole("ROLE_ADMIN");
                admin.setStatus("ACTIVE");
                userRepository.save(admin);
            }
            if (!userRepository.existsByEmail("seller@test.com")) {
                User seller = new User();
                seller.setEmail("seller@test.com");
                seller.setPasswordHash(passwordEncoder.encode("seller"));
                seller.setFirstName("Иван");
                seller.setLastName("Продавец");
                seller.setRole("ROLE_SELLER");
                seller.setStatus("ACTIVE");
                userRepository.save(seller);
            }

            String[] categoryNames = {
                    "Electronics",
                    "Fashion",
                    "Collectibles",
                    "Art & Antiques",
                    "Home & Garden",
                    "Motors",
                    "Books",
                    "Sports"
            };
            for (String name : categoryNames) {
                if (!categoryRepository.existsByName(name)) {
                    Category cat = new Category();
                    cat.setName(name);
                    cat.setDescription("Category for " + name);
                    categoryRepository.save(cat);
                }
            }
            System.out.println("✅ Категории обновлены!");
        };
    }
}
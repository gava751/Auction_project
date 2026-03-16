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

            // 1. Пользователь
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

            // 2. Категория
            Category category;
            if (!categoryRepository.existsByName("Electronics")) {
                category = new Category();
                category.setName("Electronics");
                category.setDescription("Гаджеты и девайсы");
                category = categoryRepository.save(category);
            } else {
                category = categoryRepository.findByName("Electronics");
            }

            // 3. Лоты (передаем category.getId() вторым параметром)
            if (lotRepository.count() == 0) {
                Lot lot1 = new Lot(buyer.getId(), category.getId(), "Apple MacBook Pro M3 Max",
                        new BigDecimal("2000.00"), new BigDecimal("50.00"), LocalDateTime.now().plusDays(1));

                Lot lot2 = new Lot(buyer.getId(), category.getId(), "Sony PlayStation 5 Pro",
                        new BigDecimal("450.00"), new BigDecimal("10.00"), LocalDateTime.now().plusDays(7));

                Lot lot3 = new Lot(buyer.getId(), category.getId(), "Часы Rolex Submariner",
                        new BigDecimal("8500.00"), new BigDecimal("100.00"), LocalDateTime.now().plusHours(2));

                lotRepository.save(lot1);
                lotRepository.save(lot2);
                lotRepository.save(lot3);
                System.out.println("✅ Тестовые данные успешно загружены!");
            }
        };
    }
}
package com.auction.platform.integration;

import com.auction.platform.domain.User;
import com.auction.platform.dto.BidRequest;
import com.auction.platform.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Чтобы данные после теста очищались сами
class AuctionIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Создаем реального пользователя в БД для теста
        if (!userRepository.existsByEmail("user@test.com")) {
            User user = new User();
            user.setEmail("user@test.com");
            user.setFirstName("Test");
            user.setLastName("User");
            user.setRole("ROLE_BUYER");
            user.setStatus("ACTIVE");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "BUYER")
    @DisplayName("Интеграционный тест: Попытка сделать ставку на несуществующий лот")
    void testPlaceBidOnMissingLot() throws Exception {
        BidRequest request = new BidRequest(999L, new BigDecimal("200.00"));

        mockMvc.perform(post("/api/v1/bids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Теперь вернется 400 благодаря ExceptionHandler
    }
}
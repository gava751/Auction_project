package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.repository.LotRepository;
import com.auction.platform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock private LotRepository lotRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private LotService lotService;

    @Test
    @DisplayName("Продавец может удалить только свой лот")
    void shouldThrowExceptionWhenDeletingForeignLot() {
        User hacker = new User();
        hacker.setId(99L);
        hacker.setEmail("hacker@test.com");

        Lot lot = new Lot(1L, 1L, "MacBook", BigDecimal.TEN, BigDecimal.ONE, LocalDateTime.now().plusDays(1));
        lot.setId(5L);

        when(lotRepository.findById(5L)).thenReturn(Optional.of(lot));
        when(userRepository.findByEmail("hacker@test.com")).thenReturn(Optional.of(hacker));

        assertThrows(RuntimeException.class, () -> lotService.deleteLot(5L, "hacker@test.com"));

        verify(lotRepository, never()).delete(any());
    }
}
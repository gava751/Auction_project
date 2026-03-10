package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.dto.BidRequest;
import com.auction.platform.exception.InvalidBidException;
import com.auction.platform.repository.AutoBidRepository;
import com.auction.platform.repository.BidRepository;
import com.auction.platform.repository.LotRepository;
import com.auction.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock private LotRepository lotRepository;
    @Mock private BidRepository bidRepository;
    @Mock private UserRepository userRepository;
    @Mock private AutoBidRepository autoBidRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private BidService bidService;

    private User buyer;
    private Lot activeLot;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setEmail("buyer@example.com");

        activeLot = new Lot(2L, 1L, "Test Item", new BigDecimal("100.00"), new BigDecimal("10.00"), LocalDateTime.now().plusDays(1));
        activeLot.setId(10L);
    }

    @Test
    @DisplayName("Ставка должна быть отклонена, если она меньше текущей цены + шаг")
    void shouldThrowExceptionWhenBidIsTooLow() {
        // Given
        BidRequest lowBid = new BidRequest(10L, new BigDecimal("105.00")); // Текущая 100 + шаг 10 = минимум 110
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(buyer));
        when(lotRepository.findByIdWithPessimisticLock(10L)).thenReturn(Optional.of(activeLot));

        // When & Then
        assertThrows(InvalidBidException.class, () -> {
            bidService.placeManualBid("buyer@example.com", lowBid);
        });
    }

    @Test
    @DisplayName("Ставка должна быть принята и обновлять цену лота")
    void shouldAcceptValidBid() {
        // Given
        BigDecimal bidAmount = new BigDecimal("120.00");
        BidRequest validBid = new BidRequest(10L, bidAmount);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(buyer));
        when(lotRepository.findByIdWithPessimisticLock(10L)).thenReturn(Optional.of(activeLot));

        // When
        bidService.placeManualBid("buyer@example.com", validBid);

        // Then
        verify(bidRepository, times(1)).save(any());
        verify(lotRepository, times(1)).save(argThat(lot -> lot.getCurrentPrice().equals(bidAmount)));
        verify(eventPublisher, times(1)).publishEvent(any(Lot.class));
    }
}
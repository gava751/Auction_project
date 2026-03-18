package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.LotStatus;
import com.auction.platform.repository.LotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

    @Mock
    private LotRepository lotRepository;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    @Test
    @DisplayName("Планировщик должен находить просроченные лоты и менять их статус на COMPLETED")
    void shouldCloseExpiredAuctions() {
        Lot expiredLot = new Lot(1L, 1L, "Test Lot", BigDecimal.TEN, BigDecimal.ONE, LocalDateTime.now().minusMinutes(5));
        expiredLot.setStatus(LotStatus.ACTIVE);

        when(lotRepository.findByStatusAndEndTimeBefore(eq(LotStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredLot));

        auctionScheduler.closeExpiredAuctions();

        assertEquals(LotStatus.COMPLETED, expiredLot.getStatus());
        verify(lotRepository, times(1)).save(expiredLot);
    }
}
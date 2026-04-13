package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.LotStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

  @Test
  @DisplayName("Генерация PDF отчета должна проходить без ошибок и записывать данные в поток")
  void shouldGeneratePdfReport() throws IOException {
    ReportService reportService = new ReportService();
    MockHttpServletResponse response = new MockHttpServletResponse();

    Lot lot =
        new Lot(
            1L,
            1L,
            "Test MacBook",
            new BigDecimal("1500.00"),
            new BigDecimal("50.00"),
            LocalDateTime.now());
    lot.setId(10L);
    lot.setStatus(LotStatus.COMPLETED);

    reportService.exportLotReport(response, lot);

    byte[] content = response.getContentAsByteArray();
    assertNotNull(content);
    assertTrue(content.length > 0);
    String pdfHeader = new String(content, 0, 4);
    assertTrue(pdfHeader.contains("%PDF"));
  }
}

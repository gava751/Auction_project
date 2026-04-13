package com.auction.platform.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExternalApiServiceTest {

    @Test
    @DisplayName("Проверка получения курса валют из внешнего API")
    void shouldReturnCurrencyRate() {
        ExternalApiService service = new ExternalApiService();
        Double rate = service.getUsdToEurRate();

        assertNotNull(rate);
        assertTrue(rate > 0);
    }
}
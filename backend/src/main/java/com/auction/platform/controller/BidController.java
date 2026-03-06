package com.auction.platform.controller;

import com.auction.platform.dto.BidRequest;
import com.auction.platform.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
@Tag(name = "Bidding System", description = "API для размещения ставок и настройки авто-биддера")
public class BidController {

    private final BidService bidService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Сделать ручную ставку на лот")
    public ResponseEntity<String> placeBid(@Valid @RequestBody BidRequest request, Authentication authentication) {
        bidService.placeManualBid(authentication.getName(), request);
        return ResponseEntity.ok("Ставка успешно принята");
    }

    @PostMapping("/auto/{lotId}")
    @PreAuthorize("hasRole('BUYER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Установить лимит для авто-биддера (прокси-ставка)")
    public ResponseEntity<String> setupAutoBid(
            @PathVariable Long lotId,
            @RequestParam BigDecimal maxLimit,
            Authentication authentication) {

        bidService.setupAutoBid(authentication.getName(), lotId, maxLimit);
        return ResponseEntity.ok("Авто-биддер активирован");
    }
}
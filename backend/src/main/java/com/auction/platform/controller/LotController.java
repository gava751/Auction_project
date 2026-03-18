package com.auction.platform.controller;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.dto.LotResponse;
import com.auction.platform.service.LotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.auction.platform.service.ExternalApiService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lots")
@RequiredArgsConstructor
@Tag(name = "Lot Catalog", description = "API для управления и просмотра каталога лотов")
public class LotController {

    private final LotService lotService;
    private final ExternalApiService externalApiService;
    private final com.auction.platform.repository.UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/lots/";
    private final com.auction.platform.repository.LotRepository lotRepository;
    private final com.auction.platform.repository.BidRepository bidRepository;

    @GetMapping
    @Operation(summary = "Получить список активных лотов с фильтрацией")
    public ResponseEntity<Page<com.auction.platform.dto.LotResponse>> getActiveLots(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            java.security.Principal principal) {

        Long userId = null;
        if (principal != null) {
            userId = userRepository.findByEmail(principal.getName())
                    .map(com.auction.platform.domain.User::getId)
                    .orElse(null);
        }
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(page, size);

        return ResponseEntity.ok(lotService.getActiveLots(categoryId, search, userId, pageRequest));
    }
    @GetMapping("/{id}")
    public ResponseEntity<com.auction.platform.dto.LotResponse> getLotById(@PathVariable Long id) {
        com.auction.platform.domain.Lot lot = lotService.getLotEntityById(id);

        String winnerEmail = bidRepository.findTopByLotIdOrderByAmountDesc(id)
                .map(bid -> bid.getUser().getEmail())
                .orElse(null);

        Double rate = externalApiService.getUsdToEurRate();
        java.math.BigDecimal eurPrice = lot.getCurrentPrice().multiply(java.math.BigDecimal.valueOf(rate));

        return ResponseEntity.ok(new com.auction.platform.dto.LotResponse(
                lot.getId(),
                lot.getTitle(),
                lot.getCurrentPrice(),
                eurPrice,
                lot.getEndTime(),
                lot.getStatus().name(),
                lot.getImagePath(),
                winnerEmail
        ));
    }
    @Autowired
    private com.auction.platform.service.ReportService reportService;

    @GetMapping("/{id}/report")
    @Operation(summary = "Скачать PDF отчет о результатах торгов")
    public void downloadReport(@PathVariable Long id, java.security.Principal principal, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        if (principal == null) {
            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Необходима авторизация");
            return;
        }
        com.auction.platform.domain.User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        com.auction.platform.domain.Lot lot = lotService.getLotEntityById(id);
        Long winnerId = bidRepository.findTopByLotIdOrderByAmountDesc(id)
                .map(bid -> bid.getUser().getId())
                .orElse(-1L); // Если ставок нет, победителя нет
        if (!currentUser.getId().equals(lot.getSellerId()) && !currentUser.getId().equals(winnerId)) {
            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN, "Отчет доступен только продавцу и победителю торгов");
            return;
        }
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=lot_" + id + "_report.pdf";
        response.setHeader(headerKey, headerValue);

        reportService.exportLotReport(response, lot);
    }

    @GetMapping("/won")
    @Operation(summary = "Получить список выигранных пользователем лотов")
    public List<com.auction.platform.dto.LotResponse> getWonLots(java.security.Principal principal) {
        com.auction.platform.domain.User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        return lotRepository.findWonLots(user.getId()).stream()
                .map(lot -> {
                    com.auction.platform.dto.LotResponse res = com.auction.platform.pattern.factory.LotFactory.createResponse(lot);
                    return new com.auction.platform.dto.LotResponse(
                            res.id(), res.title(), res.currentPrice(), null,
                            res.endTime(), res.status(), res.imagePath(), user.getEmail()
                    );
                })
                .toList();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<LotResponse> createLot(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("startPrice") BigDecimal startPrice,
            @RequestParam("bidStep") BigDecimal bidStep,
            @RequestParam("endTime") String endTime,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {

        User seller = userRepository.findByEmail(principal.getName()).orElseThrow();

        Lot lot = new Lot(seller.getId(), categoryId, title, startPrice, bidStep, LocalDateTime.parse(endTime));
        lot.setDescription(description);
        lot.setCurrentPrice(startPrice);

        if (file != null && !file.isEmpty()) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());
            lot.setImagePath(UPLOAD_DIR + fileName);
        }

        return ResponseEntity.ok(lotService.createLot(lot));
    }
    @GetMapping("/my")
    public List<LotResponse> getMyLots(Principal principal) {
        User seller = userRepository.findByEmail(principal.getName()).orElseThrow();
        return lotRepository.findBySellerId(seller.getId()).stream()
                .map(com.auction.platform.pattern.factory.LotFactory::createResponse)
                .toList();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLot(@PathVariable Long id, Principal principal) {
        lotService.deleteLot(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
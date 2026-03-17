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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    @Operation(summary = "Получить список активных лотов с пагинацией")
    public ResponseEntity<Page<LotResponse>> getActiveLots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "endTime") String sortBy) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(lotService.getActiveLots(pageRequest));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Получить детальную информацию о лоте")
    public ResponseEntity<LotResponse> getLotById(@PathVariable Long id) {
        Lot lot = lotService.getLotEntityById(id);
        Double rate = externalApiService.getUsdToEurRate();
        BigDecimal eurPrice = lot.getCurrentPrice().multiply(BigDecimal.valueOf(rate));

        return ResponseEntity.ok(new LotResponse(
                lot.getId(), lot.getTitle(), lot.getCurrentPrice(), eurPrice, lot.getEndTime(), lot.getStatus().name()
        ));
    }
    @Autowired
    private com.auction.platform.service.ReportService reportService;

    @GetMapping("/{id}/report")
    @Operation(summary = "Скачать PDF отчет о результатах торгов")
    public void downloadReport(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=lot_" + id + "_report.pdf";
        response.setHeader(headerKey, headerValue);

        Lot lot = lotService.getLotEntityById(id);
        reportService.exportLotReport(response, lot);
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
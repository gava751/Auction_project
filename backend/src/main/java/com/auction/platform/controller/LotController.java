package com.auction.platform.controller;

import com.auction.platform.domain.Lot;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/lots")
@RequiredArgsConstructor
@Tag(name = "Lot Catalog", description = "API для управления и просмотра каталога лотов")
public class LotController {

    private final LotService lotService;
    private final ExternalApiService externalApiService;

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
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @Operation(summary = "Создать новый лот (только для продавцов)")
    public ResponseEntity<LotResponse> createLot(@RequestBody Lot lot) {
        return ResponseEntity.ok(lotService.createLot(lot));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Удалить/отменить лот (только для админов)")
    public ResponseEntity<Void> deleteLot(@PathVariable Long id) {
        lotService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }
}
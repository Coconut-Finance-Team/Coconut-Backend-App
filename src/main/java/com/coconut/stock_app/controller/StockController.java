// package com.coconut.stock_app.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import com.coconut.stock_app.dto.IndexDataDto;
// import com.coconut.stock_app.service.StockService;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/v1/realtime")
// public class StockController {

// private final StockService stockService;

// @GetMapping("/kospi")
// public ResponseEntity<IndexDataDto> getKospiData() {
// IndexDataDto kospiData = stockService.getKospiData();
// if (kospiData == null) {
// return ResponseEntity.noContent().build(); // 데이터가 없을 경우 204 응답
// }
// return ResponseEntity.ok(kospiData);
// }

// @GetMapping("/kosdaq")
// public ResponseEntity<IndexDataDto> getKosdaqData() {
// IndexDataDto kosdaqData = stockService.getKosdaqData();
// if (kosdaqData == null) {
// return ResponseEntity.noContent().build();
// }
// return ResponseEntity.ok(kosdaqData);
// }
// }

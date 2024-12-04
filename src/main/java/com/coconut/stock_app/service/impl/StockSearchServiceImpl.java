package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.stock.StockSearchResponse;
import com.coconut.stock_app.entity.elasticsearch.StockDocument;
import com.coconut.stock_app.repository.elasticsearch.StockSearchRepository;
import com.coconut.stock_app.service.StockSearchService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockSearchServiceImpl implements StockSearchService {

    private final StockSearchRepository searchRepository;

    @Override
    public List<StockSearchResponse> searchStocks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<StockDocument> searchResults;
            // 종목 코드로 정확한 검색 시도
            Optional<StockDocument> codeMatch = searchRepository.findByStockCode(keyword);
            if (codeMatch.isPresent()) {
                searchResults = List.of(codeMatch.get());
            } else {
                // 종목명으로 부분 검색
                searchResults = searchRepository.findByStockNameContaining(keyword);
            }

            return searchResults.stream()
                    .map(StockSearchResponse::fromDocument)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search stocks with keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }
}
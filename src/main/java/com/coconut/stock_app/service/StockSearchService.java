package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.stock.StockSearchResponse;
import java.util.List;

public interface StockSearchService {
    List<StockSearchResponse> searchStocks(String keyword);
}
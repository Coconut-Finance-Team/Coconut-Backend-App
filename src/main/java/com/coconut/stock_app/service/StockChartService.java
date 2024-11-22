package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import java.util.List;

public interface StockChartService {
    List<StockChartResponse> getStockChartData(String stockCode);
    List<StockChartResponse> getStockChartDataAfter(String stockCode, String time);
}
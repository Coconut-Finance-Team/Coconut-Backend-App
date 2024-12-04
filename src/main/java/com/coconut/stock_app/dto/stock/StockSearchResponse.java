package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.elasticsearch.StockDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchResponse {

    private String stockCode;  // 종목 코드
    private String stockName;  // 종목 이름

    public static StockSearchResponse fromDocument(StockDocument document) {
        return StockSearchResponse.builder()
                .stockCode(document.getStockCode())
                .stockName(document.getStockName())
                .build();
    }
}

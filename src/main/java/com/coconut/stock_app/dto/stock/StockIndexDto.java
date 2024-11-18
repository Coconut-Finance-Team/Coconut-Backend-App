package com.coconut.stock_app.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockIndexDto {
    private String marketCode; // 업종 구분 코드 (예: 코스피 0001, 코스닥 0002)
    private String marketTime; // 영업 시간
    private String currentIndex; // 현재가 지수
}

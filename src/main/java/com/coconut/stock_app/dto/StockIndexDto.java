package com.coconut.stock_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 실시간 국내지수 데이터 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockIndexDto {

    private String marketCode; // 업종 구분 코드 (예: 코스피 0001, 코스닥 0002)
    private String marketTime; // 영업 시간
    private String currentIndex; // 현재가 지수
    private String changeSign; // 전일 대비 부호 (예: 1: 상승, 2: 하락, 3: 보합)
    private String changeValue; // 업종 지수 전일 대비
    private String cumulativeVolume; // 누적 거래량
    private String cumulativeAmount; // 누적 거래 대금
    private String perTradeVolume; // 건별 거래량
    private String perTradeAmount; // 건별 거래 대금
    private String previousDayRate; // 전일 대비율
    private String openingIndex; // 시가 지수
    private String highIndex; // 지수 최고가
    private String lowIndex; // 지수 최저가
    private String openVsCurrent; // 시가 대비 지수 현재가
    private String openVsSign; // 시가 대비 지수 부호
    private String highVsCurrent; // 최고가 대비 지수 현재가
    private String highVsSign; // 최고가 대비 지수 부호
    private String lowVsCurrent; // 최저가 대비 지수 현재가
    private String lowVsSign; // 최저가 대비 지수 부호
    private String prevCloseVsOpenRate; // 전일 종가 대비 시가 비율
    private String prevCloseVsHighRate; // 전일 종가 대비 최고가 비율
    private String prevCloseVsLowRate; // 전일 종가 대비 최저가 비율
    private String upperLimitCount; // 상한 종목 수
    private String risingCount; // 상승 종목 수
    private String unchangedCount; // 보합 종목 수
    private String fallingCount; // 하락 종목 수
    private String lowerLimitCount; // 하한 종목 수
    private String strongRisingCount; // 기세 상승 종목수
    private String strongFallingCount; // 기세 하락 종목수
    private String tickDifference; // TICK 대비
}

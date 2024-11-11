package com.coconut.stock_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexDataDto {
    private int encrypt;
    private String trId;
    private int dataCount;
    private IndexDetailDto data;
}
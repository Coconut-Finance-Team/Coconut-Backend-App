package com.coconut.stock_app.dto.ipo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IPORequestDTO {
    private Long ipoId;
    private Long quantity;
}

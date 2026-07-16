package com.findex.team02.indexdata.dto.response;

import java.math.BigDecimal;

public record IndexPerformanceDto(
        BigDecimal versus,
        BigDecimal fluctuationRate,
        BigDecimal currentPrice,
        BigDecimal beforePrice
) {
}

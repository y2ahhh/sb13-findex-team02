package com.findex.team02.indexdata.dto.response;

import java.math.BigDecimal;

public record FavoriteIndexPerformanceDto(
        Long indexInfoId,
        String indexClassification,
        String indexName,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        BigDecimal currentPrice,
        BigDecimal beforePrice
) {
}
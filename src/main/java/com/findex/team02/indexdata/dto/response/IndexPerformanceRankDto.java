package com.findex.team02.indexdata.dto.response;

public record IndexPerformanceRankDto(
        Long indexInfoId,
        String indexClassification,
        String indexName,
        Integer rank,
        IndexPerformanceDto performance
) {
}

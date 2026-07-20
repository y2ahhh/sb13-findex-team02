package com.findex.team02.indexdata.dto.response;

public record IndexPerformanceRankDto(
        IndexPerformanceDto performance,
        Integer rank
) {
}
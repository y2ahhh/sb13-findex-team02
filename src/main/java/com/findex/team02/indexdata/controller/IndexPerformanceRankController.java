package com.findex.team02.indexdata.controller;

import com.findex.team02.indexdata.dto.response.IndexPerformanceRankDto;
import com.findex.team02.indexdata.entity.PerformancePeriodType;
import com.findex.team02.indexdata.service.IndexPerformanceRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IndexPerformanceRankController {

    private final IndexPerformanceRankService indexPerformanceRankService;

    @GetMapping("/api/index-data/performance/rank")
    public List<IndexPerformanceRankDto> getPerformanceRank(
            @RequestParam Long indexInfoId,
            @RequestParam(defaultValue = "DAILY") PerformancePeriodType periodType,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return indexPerformanceRankService.getPerformanceRank(indexInfoId, periodType, limit);
    }
}
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

    /**
     * 지수 성과 랭킹을 조회한다.
     *
     * indexInfoId가 있으면:
     * - 해당 지수의 성과와 전체 순위를 조회한다.
     *
     * indexInfoId가 없으면:
     * - 전체 지수의 성과를 계산하고 상위 limit개를 조회한다.
     */
    @GetMapping("/api/index-data/performance/rank")
    public List<IndexPerformanceRankDto> getPerformanceRank(

            // 선택값이다.
            // 값이 없으면 전체 지수 성과 랭킹을 조회한다.
            @RequestParam(required = false)
            Long indexInfoId,

            // 성과 계산 기간이다.
            // 요청값이 없으면 DAILY를 사용한다.
            @RequestParam(defaultValue = "DAILY")
            PerformancePeriodType periodType,

            // 최대 조회 개수이다.
            // 요청값이 없으면 10개를 조회한다.
            @RequestParam(defaultValue = "10")
            Integer limit
    ) {
        return indexPerformanceRankService.getPerformanceRank(
                indexInfoId,
                periodType,
                limit
        );
    }
}
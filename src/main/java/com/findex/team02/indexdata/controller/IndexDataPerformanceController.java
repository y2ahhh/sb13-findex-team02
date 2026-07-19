package com.findex.team02.indexdata.controller;

import com.findex.team02.indexdata.service.IndexDataPerformanceService;
import com.findex.team02.indexinfo.dto.response.IndexChartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataPerformanceController {

    // 차트 조회의 기본 기간은 Swagger에서 확인한 기본 선택지에 맞춰 monthly로 사용한다.
    private static final String DEFAULT_PERIOD_TYPE = "monthly";

    private final IndexDataPerformanceService indexDataPerformanceService;

    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> getChart(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = DEFAULT_PERIOD_TYPE) String periodType
    ) {
        IndexChartDto response = indexDataPerformanceService.getChart(id, periodType);
        return ResponseEntity.ok(response);
    }
}
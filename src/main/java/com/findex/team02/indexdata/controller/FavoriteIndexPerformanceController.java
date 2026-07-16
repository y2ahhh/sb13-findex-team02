package com.findex.team02.indexdata.controller;

import com.findex.team02.indexdata.dto.response.FavoriteIndexPerformanceDto;
import com.findex.team02.indexdata.entity.PerformancePeriodType;
import com.findex.team02.indexdata.service.FavoriteIndexPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteIndexPerformanceController {

    private final FavoriteIndexPerformanceService favoriteIndexPerformanceService;

    @GetMapping("/api/index-data/performance/favorite")
    public List<FavoriteIndexPerformanceDto> getFavoriteIndexPerformance(
            @RequestParam(defaultValue = "DAILY")
            PerformancePeriodType periodType
    ) {
        return favoriteIndexPerformanceService.getFavoriteIndexPerformance(periodType);
    }
}
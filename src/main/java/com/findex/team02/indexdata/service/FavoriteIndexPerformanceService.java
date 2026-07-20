package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.response.FavoriteIndexPerformanceDto;
import com.findex.team02.indexdata.entity.PerformancePeriodType;

import java.util.List;

public interface FavoriteIndexPerformanceService {

    List<FavoriteIndexPerformanceDto> getFavoriteIndexPerformance(
            PerformancePeriodType periodType
    );
}
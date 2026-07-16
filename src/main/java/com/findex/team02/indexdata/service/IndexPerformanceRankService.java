package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.response.IndexPerformanceRankDto;
import com.findex.team02.indexdata.entity.PerformancePeriodType;

import java.util.List;

public interface IndexPerformanceRankService {

    List<IndexPerformanceRankDto> getPerformanceRank(
            Long indexInfoId,
            PerformancePeriodType periodType,
            Integer limit
    );
}

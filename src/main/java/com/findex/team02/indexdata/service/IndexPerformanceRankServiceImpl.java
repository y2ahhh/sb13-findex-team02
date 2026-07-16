package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.response.IndexPerformanceDto;
import com.findex.team02.indexdata.dto.response.IndexPerformanceRankDto;
import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexdata.entity.PerformancePeriodType;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexPerformanceRankServiceImpl implements IndexPerformanceRankService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    @Override
    public List<IndexPerformanceRankDto> getPerformanceRank(
            Long indexInfoId,
            PerformancePeriodType periodType,
            Integer limit
    ) {
        validateRequest(indexInfoId, periodType, limit);

        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지수 정보입니다."));

        IndexData currentData = indexDataRepository.findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId)
                .orElseThrow(() -> new IllegalArgumentException("지수 데이터가 존재하지 않습니다."));

        LocalDate beforeDate = calculateBeforeDate(currentData.getBaseDate(), periodType);

        IndexData beforeData = indexDataRepository
                .findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, beforeDate)
                .orElseThrow(() -> new IllegalArgumentException("비교할 이전 지수 데이터가 존재하지 않습니다."));

        BigDecimal beforePrice = beforeData.getClosingPrice();
        BigDecimal currentPrice = currentData.getClosingPrice();

        if (beforePrice == null || currentPrice == null || beforePrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("성과 계산에 필요한 종가 데이터가 올바르지 않습니다.");
        }

        BigDecimal versus = currentPrice.subtract(beforePrice);

        BigDecimal fluctuationRate = versus
                .divide(beforePrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        IndexPerformanceDto performance = new IndexPerformanceDto(
                versus,
                fluctuationRate,
                currentPrice,
                beforePrice
        );

        IndexPerformanceRankDto result = new IndexPerformanceRankDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                1,
                performance
        );

        return List.of(result);
    }

    private LocalDate calculateBeforeDate(LocalDate currentDate, PerformancePeriodType periodType) {
        return switch (periodType) {
            case DAILY -> currentDate.minusDays(1);
            case WEEKLY -> currentDate.minusWeeks(1);
            case MONTHLY -> currentDate.minusMonths(1);
        };
    }

    private void validateRequest(Long indexInfoId, PerformancePeriodType periodType, Integer limit) {
        if (indexInfoId == null) {
            throw new IllegalArgumentException("indexInfoId는 필수입니다.");
        }

        if (periodType == null) {
            throw new IllegalArgumentException("periodType은 필수입니다.");
        }

        if (limit == null || limit <= 0) {
            throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");
        }
    }
}
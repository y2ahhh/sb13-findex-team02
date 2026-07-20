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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexPerformanceRankServiceImpl
        implements IndexPerformanceRankService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    @Override
    public List<IndexPerformanceRankDto> getPerformanceRank(
            Long indexInfoId,
            PerformancePeriodType periodType,
            Integer limit
    ) {
        validateRequest(periodType, limit);

        List<IndexPerformanceRankDto> performanceList = new ArrayList<>();

        for (IndexInfo indexInfo : indexInfoRepository.findAll()) {
            IndexPerformanceRankDto performanceRank =
                    createPerformanceRank(indexInfo, periodType);

            if (performanceRank != null) {
                performanceList.add(performanceRank);
            }
        }

        performanceList.sort(
                Comparator.comparing(
                        dto -> dto.performance().fluctuationRate(),
                        Comparator.reverseOrder()
                )
        );

        List<IndexPerformanceRankDto> rankedResult = new ArrayList<>();

        for (int i = 0; i < performanceList.size(); i++) {
            IndexPerformanceRankDto dto = performanceList.get(i);

            rankedResult.add(
                    new IndexPerformanceRankDto(
                            dto.performance(),
                            i + 1
                    )
            );
        }

        if (indexInfoId != null) {
            return rankedResult.stream()
                    .filter(dto ->
                            Objects.equals(
                                    dto.performance().indexInfoId(),
                                    indexInfoId
                            )
                    )
                    .toList();
        }

        return rankedResult.stream()
                .limit(limit)
                .toList();
    }

    private IndexPerformanceRankDto createPerformanceRank(
            IndexInfo indexInfo,
            PerformancePeriodType periodType
    ) {
        Long indexInfoId = indexInfo.getId();

        IndexData currentData = indexDataRepository
                .findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId)
                .orElse(null);

        if (currentData == null || currentData.getBaseDate() == null) {
            return null;
        }

        LocalDate beforeDate =
                calculateBeforeDate(currentData.getBaseDate(), periodType);

        IndexData beforeData = indexDataRepository
                .findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
                        indexInfoId,
                        beforeDate
                )
                .orElse(null);

        if (beforeData == null) {
            return null;
        }

        BigDecimal currentPrice = currentData.getClosingPrice();
        BigDecimal beforePrice = beforeData.getClosingPrice();

        if (currentPrice == null
                || beforePrice == null
                || beforePrice.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal versus = currentPrice.subtract(beforePrice);

        BigDecimal fluctuationRate = versus
                .divide(beforePrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        IndexPerformanceDto performance = new IndexPerformanceDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                versus,
                fluctuationRate,
                currentPrice,
                beforePrice
        );

        return new IndexPerformanceRankDto(
                performance,
                0
        );
    }

    private LocalDate calculateBeforeDate(
            LocalDate currentDate,
            PerformancePeriodType periodType
    ) {
        return switch (periodType) {
            case DAILY -> currentDate.minusDays(1);
            case WEEKLY -> currentDate.minusWeeks(1);
            case MONTHLY -> currentDate.minusMonths(1);
        };
    }

    private void validateRequest(
            PerformancePeriodType periodType,
            Integer limit
    ) {
        if (periodType == null) {
            throw new IllegalArgumentException(
                    "periodType은 필수입니다."
            );
        }

        if (limit == null || limit <= 0) {
            throw new IllegalArgumentException(
                    "limit은 1 이상이어야 합니다."
            );
        }
    }
}
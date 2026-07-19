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

        // 모든 지수 정보를 조회한 뒤 성과 계산이 가능한 지수만 목록에 추가한다.
        for (IndexInfo indexInfo : indexInfoRepository.findAll()) {
            IndexPerformanceRankDto performanceRank =
                    createPerformanceRank(indexInfo, periodType);

            if (performanceRank != null) {
                performanceList.add(performanceRank);
            }
        }

        // 등락률이 높은 지수부터 정렬한다.
        performanceList.sort(
                Comparator.comparing(
                        dto -> dto.performance().fluctuationRate(),
                        Comparator.reverseOrder()
                )
        );

        // 정렬된 순서에 따라 실제 순위를 부여한다.
        List<IndexPerformanceRankDto> rankedResult = new ArrayList<>();

        for (int i = 0; i < performanceList.size(); i++) {
            IndexPerformanceRankDto dto = performanceList.get(i);

            rankedResult.add(
                    new IndexPerformanceRankDto(
                            dto.indexInfoId(),
                            dto.indexClassification(),
                            dto.indexName(),
                            i + 1,
                            dto.performance()
                    )
            );
        }

        // 특정 지수 ID가 전달되면 해당 지수의 순위만 반환한다.
        if (indexInfoId != null) {
            return rankedResult.stream()
                    .filter(dto ->
                            Objects.equals(dto.indexInfoId(), indexInfoId)
                    )
                    .toList();
        }

        // 지수 ID가 없으면 전체 랭킹 중 limit만큼 반환한다.
        return rankedResult.stream()
                .limit(limit)
                .toList();
    }

    /**
     * 지수 하나의 기간별 성과를 계산한다.
     *
     * 현재 데이터 또는 비교 데이터가 존재하지 않으면
     * 랭킹 계산 대상에서 제외하기 위해 null을 반환한다.
     */
    private IndexPerformanceRankDto createPerformanceRank(
            IndexInfo indexInfo,
            PerformancePeriodType periodType
    ) {
        Long indexInfoId = indexInfo.getId();

        // 해당 지수의 가장 최신 데이터를 조회한다.
        IndexData currentData = indexDataRepository
                .findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId)
                .orElse(null);

        if (currentData == null || currentData.getBaseDate() == null) {
            return null;
        }

        // 일간, 주간, 월간에 맞는 비교 기준일을 계산한다.
        LocalDate beforeDate =
                calculateBeforeDate(currentData.getBaseDate(), periodType);

        // 비교 기준일과 같거나 이전인 데이터 중 가장 가까운 데이터를 조회한다.
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

        // 성과 계산이 불가능한 데이터는 랭킹에서 제외한다.
        if (currentPrice == null
                || beforePrice == null
                || beforePrice.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // 현재 종가와 이전 종가의 차이를 계산한다.
        BigDecimal versus = currentPrice.subtract(beforePrice);

        // 등락률을 계산한다.
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

        // 순위는 전체 정렬 후 다시 부여하므로 우선 0으로 설정한다.
        return new IndexPerformanceRankDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                0,
                performance
        );
    }

    /**
     * 성과 기간 유형에 따라 비교 기준일을 계산한다.
     */
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

    /**
     * 요청값을 검증한다.
     *
     * indexInfoId는 선택값이므로 검증하지 않는다.
     */
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
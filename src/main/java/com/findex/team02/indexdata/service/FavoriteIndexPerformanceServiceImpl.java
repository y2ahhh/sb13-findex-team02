package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.response.FavoriteIndexPerformanceDto;
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
import java.util.Objects;

/**
 * 관심 지수 성과 조회 서비스 구현체
 *
 * 즐겨찾기로 등록된 지수들을 조회한 뒤,
 * 각 지수의 기간별 성과를 계산하여 반환한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteIndexPerformanceServiceImpl
        implements FavoriteIndexPerformanceService {

    /**
     * 지수 정보 조회를 담당하는 Repository
     */
    private final IndexInfoRepository indexInfoRepository;

    /**
     * 지수 데이터 조회를 담당하는 Repository
     */
    private final IndexDataRepository indexDataRepository;

    /**
     * 관심 지수들의 기간별 성과를 조회한다.
     *
     * @param periodType 성과 비교 기간
     *                   DAILY, WEEKLY, MONTHLY 중 하나
     * @return 관심 지수 성과 DTO 목록
     */
    @Override
    public List<FavoriteIndexPerformanceDto> getFavoriteIndexPerformance(
            PerformancePeriodType periodType
    ) {
        // 요청으로 전달된 기간 타입을 검증한다.
        validatePeriodType(periodType);

        // favorite 값이 true인 관심 지수 목록을 조회한다.
        List<IndexInfo> favoriteIndexes =
                indexInfoRepository.findAllByFavoriteTrue();

        // 관심 지수별 성과를 계산하고,
        // 성과 계산이 불가능한 지수는 결과에서 제외한다.
        return favoriteIndexes.stream()
                .map(indexInfo -> calculatePerformance(indexInfo, periodType))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 하나의 관심 지수에 대한 성과를 계산한다.
     *
     * @param indexInfo 성과를 계산할 지수 정보
     * @param periodType 성과 비교 기간
     * @return 계산된 관심 지수 성과 DTO
     *         데이터가 부족하거나 올바르지 않으면 null
     */
    private FavoriteIndexPerformanceDto calculatePerformance(
            IndexInfo indexInfo,
            PerformancePeriodType periodType
    ) {
        Long indexInfoId = indexInfo.getId();

        // 해당 지수의 가장 최신 데이터를 조회한다.
        IndexData currentData = indexDataRepository
                .findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId)
                .orElse(null);

        // 최신 데이터가 없으면 성과를 계산할 수 없다.
        if (currentData == null) {
            return null;
        }

        // 최신 데이터의 기준일자를 기준으로 비교 대상 날짜를 계산한다.
        LocalDate beforeDate = calculateBeforeDate(
                currentData.getBaseDate(),
                periodType
        );

        // 비교 대상 날짜 이전 또는 같은 날짜 중 가장 가까운 데이터를 조회한다.
        IndexData beforeData = indexDataRepository
                .findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
                        indexInfoId,
                        beforeDate
                )
                .orElse(null);

        // 비교할 이전 데이터가 없으면 성과를 계산할 수 없다.
        if (beforeData == null) {
            return null;
        }

        // 현재 종가와 비교 대상 종가를 가져온다.
        BigDecimal beforePrice = beforeData.getClosingPrice();
        BigDecimal currentPrice = currentData.getClosingPrice();

        // 종가가 null이거나 이전 종가가 0이면 계산할 수 없다.
        if (beforePrice == null
                || currentPrice == null
                || beforePrice.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // 전일 대비 값을 계산한다.
        BigDecimal versus = currentPrice.subtract(beforePrice);

        // 등락률을 계산한다.
        // 계산식: (현재가 - 이전가) / 이전가 × 100
        BigDecimal fluctuationRate = versus
                .divide(beforePrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        // 계산된 성과 정보를 DTO로 변환하여 반환한다.
        return new FavoriteIndexPerformanceDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                versus,
                fluctuationRate,
                currentPrice,
                beforePrice
        );
    }

    /**
     * 기간 타입에 따라 비교 대상 날짜를 계산한다.
     *
     * @param currentDate 최신 데이터 기준일자
     * @param periodType 비교 기간 타입
     * @return 비교 대상 날짜
     */
    private LocalDate calculateBeforeDate(
            LocalDate currentDate,
            PerformancePeriodType periodType
    ) {
        return switch (periodType) {
            // 일간 성과는 최신 기준일자의 하루 전과 비교한다.
            case DAILY -> currentDate.minusDays(1);

            // 주간 성과는 최신 기준일자의 일주일 전과 비교한다.
            case WEEKLY -> currentDate.minusWeeks(1);

            // 월간 성과는 최신 기준일자의 한 달 전과 비교한다.
            case MONTHLY -> currentDate.minusMonths(1);
        };
    }

    /**
     * 기간 타입 요청값을 검증한다.
     *
     * @param periodType 요청으로 전달된 기간 타입
     */
    private void validatePeriodType(PerformancePeriodType periodType) {
        if (periodType == null) {
            throw new IllegalArgumentException("periodType은 필수입니다.");
        }
    }
}
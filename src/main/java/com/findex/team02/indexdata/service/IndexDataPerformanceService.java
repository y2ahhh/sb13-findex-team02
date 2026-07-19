package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.response.ChartDataPointDto;
import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.dto.response.IndexChartDto;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataPerformanceService {

    private static final int FIVE_DAY_MOVING_AVERAGE_WINDOW = 5;
    private static final int TWENTY_DAY_MOVING_AVERAGE_WINDOW = 20;
    private static final int MOVING_AVERAGE_SCALE = 2;

    private static final String INDEX_INFO_NOT_FOUND_MESSAGE =
            "존재하지 않는 지수 정보입니다. id=";

    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;

    public IndexChartDto getChart(
            Long indexInfoId,
            String periodTypeValue
    ) {
        IndexChartDto.ChartPeriodType periodType =
                IndexChartDto.ChartPeriodType.from(periodTypeValue);

        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                INDEX_INFO_NOT_FOUND_MESSAGE + indexInfoId
                        )
                );

        List<IndexData> indexDataRows =
                findChartDataRows(indexInfoId, periodType);

        List<ChartDataPointDto> dataPoints =
                toDataPoints(indexDataRows);

        List<ChartDataPointDto> ma5DataPoints =
                calculateMovingAverage(
                        indexDataRows,
                        FIVE_DAY_MOVING_AVERAGE_WINDOW
                );

        List<ChartDataPointDto> ma20DataPoints =
                calculateMovingAverage(
                        indexDataRows,
                        TWENTY_DAY_MOVING_AVERAGE_WINDOW
                );

        return new IndexChartDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                periodType,
                dataPoints,
                ma5DataPoints,
                ma20DataPoints
        );
    }

    private List<IndexData> findChartDataRows(
            Long indexInfoId,
            IndexChartDto.ChartPeriodType periodType
    ) {
        return indexDataRepository
                .findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId)
                .map(latestIndexData -> {
                    LocalDate endDate = latestIndexData.getBaseDate();
                    LocalDate startDate =
                            calculateStartDate(endDate, periodType);

                    return indexDataRepository
                            .findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
                                    indexInfoId,
                                    startDate,
                                    endDate
                            )
                            .stream()
                            .sorted(
                                    Comparator.comparing(
                                            IndexData::getBaseDate
                                    )
                            )
                            .toList();
                })
                .orElseGet(List::of);
    }

    private LocalDate calculateStartDate(
            LocalDate endDate,
            IndexChartDto.ChartPeriodType periodType
    ) {
        return switch (periodType) {
            case MONTHLY -> endDate.minusMonths(1);
            case QUARTERLY -> endDate.minusMonths(3);
            case YEARLY -> endDate.minusYears(1);
        };
    }

    private List<ChartDataPointDto> toDataPoints(
            List<IndexData> indexDataRows
    ) {
        List<ChartDataPointDto> dataPoints = new ArrayList<>();

        for (IndexData indexData : indexDataRows) {
            dataPoints.add(
                    new ChartDataPointDto(
                            indexData.getBaseDate(),
                            indexData.getClosingPrice()
                    )
            );
        }

        return dataPoints;
    }

    private List<ChartDataPointDto> calculateMovingAverage(
            List<IndexData> indexDataRows,
            int windowSize
    ) {
        if (indexDataRows.size() < windowSize) {
            return List.of();
        }

        List<ChartDataPointDto> movingAveragePoints =
                new ArrayList<>();

        for (
                int currentIndex = windowSize - 1;
                currentIndex < indexDataRows.size();
                currentIndex++
        ) {
            BigDecimal sum = BigDecimal.ZERO;

            for (
                    int targetIndex = currentIndex - windowSize + 1;
                    targetIndex <= currentIndex;
                    targetIndex++
            ) {
                BigDecimal closingPrice =
                        indexDataRows
                                .get(targetIndex)
                                .getClosingPrice();

                if (closingPrice == null) {
                    sum = null;
                    break;
                }

                sum = sum.add(closingPrice);
            }

            if (sum == null) {
                continue;
            }

            BigDecimal average = sum.divide(
                    BigDecimal.valueOf(windowSize),
                    MOVING_AVERAGE_SCALE,
                    RoundingMode.HALF_UP
            );

            movingAveragePoints.add(
                    new ChartDataPointDto(
                            indexDataRows
                                    .get(currentIndex)
                                    .getBaseDate(),
                            average
                    )
            );
        }

        return movingAveragePoints;
    }
}
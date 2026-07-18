package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataCsvExportServiceImpl implements IndexDataCsvExportService {

    private final IndexDataRepository indexDataRepository;

    // CSV export에서 정렬 가능한 필드 목록
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "baseDate",
            "marketPrice",
            "closingPrice",
            "highPrice",
            "lowPrice",
            "versus",
            "fluctuationRate",
            "tradingQuantity",
            "tradingPrice",
            "marketTotalAmount"
    );

    /**
     * 지수 데이터를 CSV 파일로 내보내기 위한 byte 배열을 생성한다.
     *
     * 지원하는 조회 조건:
     * 1. 지수와 날짜 모두 선택: 특정 지수의 특정 기간 데이터 조회
     * 2. 지수만 선택: 특정 지수의 전체 기간 데이터 조회
     * 3. 날짜만 선택: 모든 지수의 특정 기간 데이터 조회
     * 4. 아무것도 선택하지 않음: 모든 지수의 전체 데이터 조회
     */
    @Override
    public byte[] exportCsv(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            String sortField,
            String sortDirection
    ) {
        validateDateRange(startDate, endDate);

        String validatedSortField = validateSortField(sortField);
        Sort.Direction direction = validateSortDirection(sortDirection);
        Sort sort = Sort.by(direction, validatedSortField);

        List<IndexData> indexDataList = findIndexData(
                indexInfoId,
                startDate,
                endDate,
                sort
        );

        String csv = createCsv(indexDataList);

        return csv.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 전달된 필터 조건에 따라 조회 메서드를 선택한다.
     */
    private List<IndexData> findIndexData(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    ) {
        boolean hasIndexInfoId = indexInfoId != null;
        boolean hasDateRange = startDate != null && endDate != null;

        // 특정 지수 + 특정 기간
        if (hasIndexInfoId && hasDateRange) {
            return indexDataRepository.findByIndexInfoIdAndBaseDateBetween(
                    indexInfoId,
                    startDate,
                    endDate,
                    sort
            );
        }

        // 특정 지수 + 전체 기간
        if (hasIndexInfoId) {
            return indexDataRepository.findByIndexInfoId(
                    indexInfoId,
                    sort
            );
        }

        // 전체 지수 + 특정 기간
        if (hasDateRange) {
            return indexDataRepository.findByBaseDateBetween(
                    startDate,
                    endDate,
                    sort
            );
        }

        // 전체 지수 + 전체 기간
        return indexDataRepository.findAll(sort);
    }

    /**
     * 날짜 범위를 검증한다.
     *
     * 날짜를 입력하지 않는 경우는 전체 기간 조회로 허용한다.
     * 시작일과 종료일 중 하나만 입력한 경우는 잘못된 요청으로 처리한다.
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        boolean onlyStartDateExists = startDate != null && endDate == null;
        boolean onlyEndDateExists = startDate == null && endDate != null;

        if (onlyStartDateExists || onlyEndDateExists) {
            throw new IllegalArgumentException(
                    "시작 일자와 종료 일자는 함께 입력해야 합니다."
            );
        }

        if (startDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "시작 일자는 종료 일자보다 늦을 수 없습니다."
            );
        }
    }

    /**
     * 정렬 필드를 검증한다.
     */
    private String validateSortField(String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return "baseDate";
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 필드입니다: " + sortField
            );
        }

        return sortField;
    }

    /**
     * 정렬 방향을 검증한다.
     */
    private Sort.Direction validateSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.isBlank()) {
            return Sort.Direction.DESC;
        }

        if (sortDirection.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        throw new IllegalArgumentException(
                "지원하지 않는 정렬 방향입니다: " + sortDirection
        );
    }

    /**
     * 지수 데이터 목록을 CSV 문자열로 변환한다.
     */
    private String createCsv(List<IndexData> indexDataList) {
        StringBuilder sb = new StringBuilder();

        // 엑셀에서 UTF-8 CSV의 한글이 깨지는 것을 방지한다.
        sb.append("\uFEFF");

        sb.append(
                "baseDate,marketPrice,closingPrice,highPrice,lowPrice,"
                        + "versus,fluctuationRate,tradingQuantity,"
                        + "tradingPrice,marketTotalAmount\n"
        );

        for (IndexData data : indexDataList) {
            sb.append(value(data.getBaseDate())).append(",");
            sb.append(value(data.getMarketPrice())).append(",");
            sb.append(value(data.getClosingPrice())).append(",");
            sb.append(value(data.getHighPrice())).append(",");
            sb.append(value(data.getLowPrice())).append(",");
            sb.append(value(data.getVersus())).append(",");
            sb.append(value(data.getFluctuationRate())).append(",");
            sb.append(value(data.getTradingQuantity())).append(",");
            sb.append(value(data.getTradingPrice())).append(",");
            sb.append(value(data.getMarketTotalAmount())).append("\n");
        }

        return sb.toString();
    }

    /**
     * null 값을 CSV에서 빈 문자열로 처리한다.
     */
    private String value(Object value) {
        return value == null ? "" : value.toString();
    }
}
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

    // CSV export에서 정렬 가능한 필드 목록이다.
    // Swagger에 명시된 정렬 필드만 허용해서 잘못된 필드명으로 정렬 요청이 들어오는 것을 방지한다.
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
     * 지수 데이터를 CSV 파일로 export하기 위한 byte 배열을 생성한다.
     *
     * 처리 흐름:
     * 1. 시작일과 종료일의 범위를 검증한다.
     * 2. 정렬 필드와 정렬 방향을 검증한다.
     * 3. 검증된 정렬 조건으로 Sort 객체를 생성한다.
     * 4. indexInfoId와 날짜 범위에 맞는 지수 데이터를 조회한다.
     * 5. 조회 결과를 CSV 문자열로 변환한다.
     * 6. CSV 문자열을 UTF-8 byte 배열로 변환해 반환한다.
     */
    @Override
    public byte[] exportCsv(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            String sortField,
            String sortDirection
    ) {
        // 시작일이 종료일보다 늦은 잘못된 요청인지 확인한다.
        validateDateRange(startDate, endDate);

        // Swagger에서 허용한 정렬 필드인지 검증한다.
        String validatedSortField = validateSortField(sortField);

        // asc 또는 desc 값인지 검증하고 Spring Data JPA Sort.Direction으로 변환한다.
        Sort.Direction direction = validateSortDirection(sortDirection);

        // Repository 조회에 사용할 동적 정렬 조건을 생성한다.
        Sort sort = Sort.by(direction, validatedSortField);

        // 특정 지수의 특정 기간 데이터를 정렬 조건에 맞게 조회한다.
        List<IndexData> indexDataList = indexDataRepository.findByIndexInfoIdAndBaseDateBetween(
                indexInfoId,
                startDate,
                endDate,
                sort
        );

        // 조회한 지수 데이터 목록을 CSV 문자열로 변환한다.
        String csv = createCsv(indexDataList);

        // CSV 문자열을 UTF-8 byte 배열로 변환해서 Controller에 전달한다.
        return csv.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 날짜 범위를 검증한다.
     * 시작일이 종료일보다 늦으면 정상적인 기간 조회가 아니므로 예외를 발생시킨다.
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 일자는 종료 일자보다 늦을 수 없습니다.");
        }
    }

    /**
     * 정렬 필드를 검증한다.
     * sortField가 없으면 기본값으로 baseDate를 사용한다.
     * 허용되지 않은 필드명이 들어오면 예외를 발생시킨다.
     */
    private String validateSortField(String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return "baseDate";
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + sortField);
        }

        return sortField;
    }

    /**
     * 정렬 방향을 검증한다.
     * sortDirection이 없으면 기본값으로 DESC를 사용한다.
     * asc, desc만 허용한다.
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

        throw new IllegalArgumentException("지원하지 않는 정렬 방향입니다: " + sortDirection);
    }

    /**
     * 지수 데이터 목록을 CSV 형식의 문자열로 변환한다.
     *
     * 첫 줄에는 CSV 헤더를 작성하고,
     * 그 아래부터는 IndexData 한 건마다 한 줄씩 데이터를 작성한다.
     */
    private String createCsv(List<IndexData> indexDataList) {
        StringBuilder sb = new StringBuilder();

        // UTF-8 BOM을 추가한다.
        // 엑셀에서 CSV 파일을 열었을 때 한글이 깨지는 문제를 줄이기 위한 처리다.
        sb.append("\uFEFF");

        // CSV 헤더를 작성한다.
        sb.append("baseDate,marketPrice,closingPrice,highPrice,lowPrice,versus,fluctuationRate,tradingQuantity,tradingPrice,marketTotalAmount\n");

        // 조회된 지수 데이터를 한 줄씩 CSV row로 변환한다.
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
     * CSV에 들어갈 값을 문자열로 변환한다.
     * null 값이 있으면 CSV에 "null"이라는 문자열이 들어가지 않도록 빈 문자열로 처리한다.
     */
    private String value(Object value) {
        return value == null ? "" : value.toString();
    }
}
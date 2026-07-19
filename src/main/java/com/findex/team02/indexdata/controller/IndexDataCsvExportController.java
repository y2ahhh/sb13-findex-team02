package com.findex.team02.indexdata.controller;

import com.findex.team02.indexdata.service.IndexDataCsvExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class IndexDataCsvExportController {

    private final IndexDataCsvExportService indexDataCsvExportService;

    /**
     * 지수 데이터를 CSV 파일로 다운로드하는 API이다.
     *
     * 지원하는 조회 방식:
     * 1. 특정 지수 + 특정 기간
     * 2. 특정 지수 + 전체 기간
     * 3. 전체 지수 + 특정 기간
     * 4. 전체 지수 + 전체 기간
     *
     * 요청 예시:
     * GET /api/index-data/export/csv
     * GET /api/index-data/export/csv?indexInfoId=1
     * GET /api/index-data/export/csv?startDate=2024-01-01&endDate=2024-12-31
     * GET /api/index-data/export/csv?indexInfoId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/api/index-data/export/csv")
    public ResponseEntity<byte[]> downloadIndexData(

            // CSV로 export할 지수 정보 ID이다.
            // 값이 없으면 모든 지수 데이터를 대상으로 한다.
            @RequestParam(required = false)
            Long indexInfoId,

            // 조회 시작 일자이다.
            // 값이 없으면 전체 기간 조회로 처리한다.
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            // 조회 종료 일자이다.
            // 값이 없으면 전체 기간 조회로 처리한다.
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            // 정렬 기준 필드이다.
            // 요청값이 없으면 baseDate를 사용한다.
            @RequestParam(defaultValue = "baseDate")
            String sortField,

            // 정렬 방향이다.
            // 요청값이 없으면 desc를 사용한다.
            @RequestParam(defaultValue = "desc")
            String sortDirection
    ) {
        byte[] csvBytes = indexDataCsvExportService.exportCsv(
                indexInfoId,
                startDate,
                endDate,
                sortField,
                sortDirection
        );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType("text/csv; charset=UTF-8")
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("index-data.csv")
                        .build()
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvBytes);
    }
}
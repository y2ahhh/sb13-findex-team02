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
     * 요청 예시:
     * GET /api/index-data/export/csv?indexInfoId=1&startDate=2024-01-01&endDate=2024-12-31&sortField=baseDate&sortDirection=desc
     *
     * 처리 흐름:
     * 1. 클라이언트가 indexInfoId, startDate, endDate, sortField, sortDirection을 전달한다.
     * 2. Controller는 요청 파라미터를 받아 Service에 전달한다.
     * 3. Service는 조건에 맞는 지수 데이터를 조회한 뒤 CSV byte 배열을 생성한다.
     * 4. Controller는 CSV 다운로드에 필요한 응답 헤더를 설정한다.
     * 5. CSV 파일을 byte[] 형태로 응답한다.
     */
    @GetMapping("/api/index-data/export/csv")
    public ResponseEntity<byte[]> downloadIndexData(
            // CSV로 export할 지수 정보 ID이다.
            @RequestParam Long indexInfoId,

            // 조회 시작 일자이다.
            // yyyy-MM-dd 형식의 문자열을 LocalDate로 변환한다.
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            // 조회 종료 일자이다.
            // yyyy-MM-dd 형식의 문자열을 LocalDate로 변환한다.
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            // 정렬 기준 필드이다.
            // 요청값이 없으면 기본값으로 baseDate를 사용한다.
            @RequestParam(defaultValue = "baseDate")
            String sortField,

            // 정렬 방향이다.
            // 요청값이 없으면 기본값으로 desc를 사용한다.
            @RequestParam(defaultValue = "desc")
            String sortDirection
    ) {
        // Service에 CSV export 생성을 요청한다.
        // 반환값은 CSV 파일 내용이 담긴 byte 배열이다.
        byte[] csvBytes = indexDataCsvExportService.exportCsv(
                indexInfoId,
                startDate,
                endDate,
                sortField,
                sortDirection
        );

        // CSV 다운로드 응답에 필요한 HTTP Header를 생성한다.
        HttpHeaders headers = new HttpHeaders();

        // 응답 데이터의 타입을 CSV로 지정한다.
        // charset=UTF-8을 함께 지정해서 인코딩 정보를 명확히 한다.
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));

        // 브라우저가 응답을 파일 다운로드로 처리하도록 설정한다.
        // 다운로드 파일명은 index-data.csv로 지정한다.
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("index-data.csv")
                        .build()
        );

        // 200 OK 상태 코드와 함께 헤더, CSV byte 배열을 응답한다.
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvBytes);
    }
}
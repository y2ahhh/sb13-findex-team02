package com.findex.team02.indexinfo.dto.response;

import com.findex.team02.indexdata.dto.response.ChartDataPointDto;

import java.util.List;

// 지수 차트 조회 응답 DTO
// 특정 지수의 차트 데이터, 5일 이동평균선, 20일 이동평균선을 함께 반환함
public record IndexChartDto (
        Long indexInfoId,
        String indexClassification,
        String indexName,
        ChartPeriodType periodType,
        // 실제 차트 데이터(날짜별 종가 데이터)
        List<ChartDataPointDto> dataPoints,
        // 5일 이동평균선 데이터(closingPrice 기준 최근 5개 데이터의 평균값)
        List<ChartDataPointDto> ma5DataPoints,
        // 20일 이동평균선 데이터(closingPrice 기준 최근 20개 데이터의 평균값)
        List<ChartDataPointDto> ma20DataPoints
) {
    // 차트 조회 기간 유형
    // Swagger에서 확인한 선택값 기준으로 monthly, quarterly, yearly 요청을 받을 수 있게 구성
    public enum ChartPeriodType {
        MONTHLY,
        QUARTERLY,
        YEARLY;

        // 요청 파라미터가 monthly, quarterly, yearly처럼 소문자로 들어와도 enum으로 변환하기 위한 메서드
        public static ChartPeriodType from(String value) {
            if (value == null || value.isBlank()) {
                return MONTHLY;
            }

            return ChartPeriodType.valueOf(value.toUpperCase());
        }
    }
}
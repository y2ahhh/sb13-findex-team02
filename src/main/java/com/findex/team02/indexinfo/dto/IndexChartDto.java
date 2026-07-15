package com.findex.team02.indexinfo.dto;

import com.findex.team02.indexdata.dto.ChartDataPointDto;

import java.util.List;

// 지수 차트 조회 응답 DTO
// 특정 지수의 차트 데이터, 5일 이동평균선, 20일 이동평균선을 함께 반환함
public class IndexChartDto {

    // 지수 정보 ID
    // index_info 테이블의 id 값
    private Long indexInfoId;

    // 지수 분류
    // 예: KRX시리즈, 테마지수 등
    private String indexClassification;

    // 지수명
    // 예: KRX 300 정보기술
    private String indexName;

    // 차트 기간 유형
    // Swagger 기준 선택값: monthly, quarterly, yearly
    // 응답에서는 MONTHLY, QUARTERLY, YEARLY 형태로 반환될 수 있음
    private ChartPeriodType periodType;

    // 실제 차트 데이터
    // 날짜별 종가 데이터를 담음
    private List<ChartDataPointDto> dataPoints;

    // 5일 이동평균선 데이터
    // closingPrice 기준 최근 5개 데이터의 평균값
    private List<ChartDataPointDto> ma5DataPoints;

    // 20일 이동평균선 데이터
    // closingPrice 기준 최근 20개 데이터의 평균값
    private List<ChartDataPointDto> ma20DataPoints;

    // 기본 생성자
    // JSON 직렬화/역직렬화나 프레임워크 사용 시 필요할 수 있음
    public IndexChartDto() {
    }

    // 지수 차트 조회 응답을 생성하는 생성자
    public IndexChartDto(
            Long indexInfoId,
            String indexClassification,
            String indexName,
            ChartPeriodType periodType,
            List<ChartDataPointDto> dataPoints,
            List<ChartDataPointDto> ma5DataPoints,
            List<ChartDataPointDto> ma20DataPoints
    ) {
        this.indexInfoId = indexInfoId;
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.periodType = periodType;
        this.dataPoints = dataPoints;
        this.ma5DataPoints = ma5DataPoints;
        this.ma20DataPoints = ma20DataPoints;
    }

    // 지수 정보 ID 반환
    public Long getIndexInfoId() {
        return indexInfoId;
    }

    // 지수 분류 반환
    public String getIndexClassification() {
        return indexClassification;
    }

    // 지수명 반환
    public String getIndexName() {
        return indexName;
    }

    // 차트 기간 유형 반환
    public ChartPeriodType getPeriodType() {
        return periodType;
    }

    // 실제 차트 데이터 반환
    public List<ChartDataPointDto> getDataPoints() {
        return dataPoints;
    }

    // 5일 이동평균 데이터 반환
    public List<ChartDataPointDto> getMa5DataPoints() {
        return ma5DataPoints;
    }

    // 20일 이동평균 데이터 반환
    public List<ChartDataPointDto> getMa20DataPoints() {
        return ma20DataPoints;
    }

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
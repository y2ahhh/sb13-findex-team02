package com.findex.team02.indexdata.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

// 차트에 표시할 데이터 포인트 DTO
// 날짜와 해당 날짜의 값으로 구성됨
public record ChartDataPointDto (
        // 차트 X축에 표시될 날짜
        LocalDate date,
        // 차트 Y축에 표시될 값
        BigDecimal value
) {

}
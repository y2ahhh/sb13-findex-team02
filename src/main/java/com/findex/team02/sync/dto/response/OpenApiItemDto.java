package com.findex.team02.sync.dto.response;

public record OpenApiItemDto(
        String basDt,       // 기준일자
        String idxNm,       // 지수명
        String idxCsf,      // 지수분류명
        String epyItmsCnt,  // 채용종목수
        String evlItmsCnt,  // 평가종목수
        String lstgMrktTotAmt, // 상장시가총액
        String trqu,        // 거래량
        String trPrc,        // 거래대금
        String clpr,        // 종가(현재지수)
        String vs,          // 대비
        String fltRt,       // 등락률
        String mkp,         // 시가지수
        String hipr,        // 고가지수
        String lopr,        // 저가지수
        String basPntm,     // 기준시점
        String basIdx,      // 기준지수
        String wghtVal      // 가중치
) {
}

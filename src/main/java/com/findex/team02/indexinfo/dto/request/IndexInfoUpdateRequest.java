package com.findex.team02.indexinfo.dto.request;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * 지수 정보 수정 요청 DTO
 */
public record IndexInfoUpdateRequest(
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex,
    Boolean favorite
) {
}
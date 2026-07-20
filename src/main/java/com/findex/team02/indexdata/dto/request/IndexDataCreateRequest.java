package com.findex.team02.indexdata.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateRequest(
    @NotNull
    Long indexInfoId,

    @NotNull
    LocalDate baseDate,

    @NotNull
    @PositiveOrZero
    BigDecimal marketPrice,

    @NotNull
    @PositiveOrZero
    BigDecimal closingPrice,

    @NotNull
    @PositiveOrZero
    BigDecimal highPrice,

    @NotNull
    @PositiveOrZero
    BigDecimal lowPrice,

    @NotNull
    BigDecimal versus,

    @NotNull
    BigDecimal fluctuationRate,

    @NotNull
    @PositiveOrZero
    Long tradingQuantity,

    @NotNull
    @PositiveOrZero
    Long tradingPrice,

    @NotNull
    @PositiveOrZero
    Long marketTotalAmount
) {

}

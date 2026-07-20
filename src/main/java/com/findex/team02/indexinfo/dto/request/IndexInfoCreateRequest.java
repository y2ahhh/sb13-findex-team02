package com.findex.team02.indexinfo.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * 지수 정보 생성 요청 DTO
 */
public record IndexInfoCreateRequest(
    @NotBlank(message = "지수 분류명은 필수입니다.")
    String indexClassification,
    @NotBlank(message = "지수명은 필수입니다.")
    String indexName,
    @NotNull(message = "채용 종목 수는 필수입니다.")
    Integer employedItemsCount,
    @NotNull(message = "기준 시점은 필수입니다.")
    LocalDate basePointInTime,
    @NotNull(message = "기준 지수는 필수입니다.")
    BigDecimal baseIndex,
    Boolean favorite
) {
  public Boolean getFavoriteOrDefault() {
    return favorite != null ? favorite : false;
  }
}
package com.findex.team02.indexinfo.dto.response;
import com.findex.team02.indexinfo.entity.IndexInfo;
/**
 * 지수 정보 요약 응답 DTO
 */
public record IndexInfoSummaryDto(
        Long id,
        String indexClassification,
        String indexName
) {
  public static IndexInfoSummaryDto from(IndexInfo entity) {
    return new IndexInfoSummaryDto(
        entity.getId(),
        entity.getIndexClassification(),
        entity.getIndexName()
    );
  }
}
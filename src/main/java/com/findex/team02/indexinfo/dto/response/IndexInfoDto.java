package com.findex.team02.indexinfo.dto.response;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.global.type.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoDto (
    Long id,
    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex,
    SourceType sourceType,
    Boolean favorite
) {

    public static IndexInfoDto from(IndexInfo indexInfo) {
        return new IndexInfoDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                indexInfo.getEmployedItemsCount(),
                indexInfo.getBasePointInTime(),
                indexInfo.getBaseIndex(),
                indexInfo.getSourceType(),
                indexInfo.getFavorite()
        );
    }

}

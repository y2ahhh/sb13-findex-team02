package com.findex.team02.indexinfo.dto.response;

import com.findex.team02.indexinfo.entity.IndexInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoDto (
    Long id,
    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex,
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
                indexInfo.getFavorite()
        );
    }

}

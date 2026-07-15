package com.findex.team02.autosync.dto.response;

public record AutoSyncConfigDto(
        Long id,
        Long indexInfoId,
        String indexName,
        String indexClassification,
        Boolean enabled
) {
}

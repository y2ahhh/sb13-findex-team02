package com.findex.team02.sync.dto.response;

import java.util.List;

public record CursorPageResponseSyncJobDto(
        List<SyncJobDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}

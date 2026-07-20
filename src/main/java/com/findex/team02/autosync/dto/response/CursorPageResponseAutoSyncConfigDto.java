package com.findex.team02.autosync.dto.response;

import java.util.List;

public record CursorPageResponseAutoSyncConfigDto(
        List<AutoSyncConfigDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}

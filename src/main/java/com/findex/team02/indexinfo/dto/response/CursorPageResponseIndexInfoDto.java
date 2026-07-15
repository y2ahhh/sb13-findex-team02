package com.findex.team02.indexinfo.dto.response;

import java.util.List;

public record CursorPageResponseIndexInfoDto (
        List<IndexInfoDto> content,
        String nextCursor,
        Long nextIdAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}

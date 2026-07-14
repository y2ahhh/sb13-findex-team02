package com.findex.team02.indexdata.dto.response;

import java.util.List;

public record CursorPageResponseIndexDataDto(
    List<IndexDataDto>content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) {

}

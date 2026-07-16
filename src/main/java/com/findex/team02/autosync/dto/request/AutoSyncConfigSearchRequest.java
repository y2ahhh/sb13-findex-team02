package com.findex.team02.autosync.dto.request;

public record AutoSyncConfigSearchRequest(
        Long indexInfoId,
        Boolean enabled,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        Integer size
) {
    public AutoSyncConfigSearchRequest {
        if (size == null) {
            size = 10;  // compact constructor에서 기본값 채움
        }
    }
}

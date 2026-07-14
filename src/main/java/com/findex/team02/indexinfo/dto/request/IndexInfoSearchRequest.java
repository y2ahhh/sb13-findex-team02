package com.findex.team02.indexinfo.dto.request;

public record IndexInfoSearchRequest(
        String indexClassification,
        String indexName,
        Boolean favorite,
        String cursor,
        Long idAfter,
        String sortField,
        String sortDirection,
        Integer size
) {
    public IndexInfoSearchRequest {
        if (size == null) {
            size = 10;
        }
        if (sortField == null) {
            sortField = "indexClassification";
        }
        if (sortDirection == null) {
            sortDirection = "asc";
        }
    }
}

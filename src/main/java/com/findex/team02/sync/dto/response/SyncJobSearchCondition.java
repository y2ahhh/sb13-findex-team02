package com.findex.team02.sync.dto.response;

import com.findex.team02.sync.entity.SyncJobResult;
import com.findex.team02.sync.entity.SyncJobType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobSearchCondition(
        SyncJobType jobType,
        Long indexInfoId,
        LocalDate baseDateFrom,
        LocalDate baseDateTo,
        String worker,
        LocalDateTime jobTimeFrom,
        LocalDateTime jobTimeTo,
        SyncJobResult status,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        Integer size
) {

    private static final String SORT_FIELD_TARGET_DATE = "targetDate";
    private static final String DEFAULT_SORT_FIELD = "jobTime";
    private static final String SORT_DIRECTION_ASC = "asc";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final int DEFAULT_PAGE_SIZE = 10;

    public SyncJobSearchCondition {
        sortField = (sortField == null) ? DEFAULT_SORT_FIELD : sortField;
        sortDirection = (sortDirection == null) ? DEFAULT_SORT_DIRECTION : sortDirection;
        size = (size == null) ? DEFAULT_PAGE_SIZE : size;
    }

    public boolean isSortByTargetDate() {
        return SORT_FIELD_TARGET_DATE.equalsIgnoreCase(sortField);
    }

    public boolean isDescending() {
        return !SORT_DIRECTION_ASC.equalsIgnoreCase(sortDirection);
    }
}
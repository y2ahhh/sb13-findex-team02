package com.findex.team02.autosync.service;

import com.findex.team02.autosync.dto.request.AutoSyncConfigUpdateRequest;
import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.dto.response.CursorPageResponseAutoSyncConfigDto;

public interface AutoSyncConfigService {

    CursorPageResponseAutoSyncConfigDto findAll(
            Long indexInfoId,
            Boolean enabled,
            Long idAfter,
            String cursor,
            String sortField,
            String sortDirection,
            Integer size
    );

    AutoSyncConfigDto update (
            Long id,
            AutoSyncConfigUpdateRequest request
    );
}

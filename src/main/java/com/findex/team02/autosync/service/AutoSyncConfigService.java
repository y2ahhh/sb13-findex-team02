package com.findex.team02.autosync.service;

import com.findex.team02.autosync.dto.request.AutoSyncConfigSearchRequest;
import com.findex.team02.autosync.dto.request.AutoSyncConfigUpdateRequest;
import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.dto.response.CursorPageResponseAutoSyncConfigDto;

public interface AutoSyncConfigService {

    CursorPageResponseAutoSyncConfigDto findAll(AutoSyncConfigSearchRequest request);

    AutoSyncConfigDto update (
            Long id,
            AutoSyncConfigUpdateRequest request
    );
}

package com.findex.team02.autosync.repository;

import com.findex.team02.autosync.dto.request.AutoSyncConfigSearchRequest;
import com.findex.team02.autosync.entity.AutoSyncConfig;

import java.util.List;

public interface AutoSyncConfigRepositoryCustom {

    List<AutoSyncConfig> findAllByCondition(AutoSyncConfigSearchRequest request);

    long countTotalElements(AutoSyncConfigSearchRequest request);

}

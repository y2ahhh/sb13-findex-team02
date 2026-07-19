package com.findex.team02.sync.service;

import com.findex.team02.sync.dto.request.IndexDataSyncRequest;
import com.findex.team02.sync.dto.response.CursorPageResponseSyncJobDto;
import com.findex.team02.sync.dto.response.SyncJobDto;
import com.findex.team02.sync.dto.response.SyncJobSearchCondition;

import java.util.List;

public interface SyncJobService {

    List<SyncJobDto> syncIndexInfo(String worker);

    List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String worker);

    CursorPageResponseSyncJobDto searchSyncJobs(SyncJobSearchCondition condition);

    List<SyncJobDto> syncLatestIndexData(List<Long> indexInfoIds, String worker);
}

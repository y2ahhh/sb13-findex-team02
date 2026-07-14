package com.findex.team02.sync.repository;

import com.findex.team02.sync.dto.response.SyncJobSearchCondition;
import com.findex.team02.sync.entity.SyncJob;

import java.util.List;

public interface SyncJobRepositoryCustom {

    List<SyncJob> search(SyncJobSearchCondition condition, int limit);

    long count(SyncJobSearchCondition condition);
}

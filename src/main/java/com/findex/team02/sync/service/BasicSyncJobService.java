package com.findex.team02.sync.service;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import com.findex.team02.sync.dto.request.IndexDataSyncRequest;
import com.findex.team02.sync.dto.response.CursorPageResponseSyncJobDto;
import com.findex.team02.sync.dto.response.OpenApiItemDto;
import com.findex.team02.sync.dto.response.SyncJobDto;
import com.findex.team02.sync.dto.response.SyncJobSearchCondition;
import com.findex.team02.sync.entity.SyncJob;
import com.findex.team02.sync.mapper.SyncJobMapper;
import com.findex.team02.sync.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicSyncJobService implements SyncJobService {

    private final OpenApiService openApiService;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoSyncExecutor indexInfoSyncExecutor;
    private final IndexDataSyncExecutor indexDataSyncExecutor;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobMapper syncJobMapper;

    @Override
    public List<SyncJobDto> syncIndexInfo(String worker) {

        List<OpenApiItemDto> apiItems = openApiService.getIndexData(LocalDate.now());
        List<SyncJob> syncJobs = new ArrayList<>();

        for (OpenApiItemDto item : apiItems) {
            try {
                syncJobs.add(indexInfoSyncExecutor.syncOne(item, worker));
            } catch (Exception e) {
                syncJobs.add(indexInfoSyncExecutor.saveFailure(worker));
            }
        }

        return syncJobMapper.toDtoList(syncJobs);
    }

    @Override
    public List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String worker) {

        List<IndexInfo> targetIndices = resolveTargetIndices(request.indexInfoIds());
        List<SyncJob> syncJobs = new ArrayList<>();

        for (LocalDate targetDate = request.baseDateFrom();
             !targetDate.isAfter(request.baseDateTo());
             targetDate = targetDate.plusDays(1)) {

            Map<String, OpenApiItemDto> itemsByIndexKey = openApiService.getIndexData(targetDate).stream()
                    .collect(Collectors.toMap(this::indexKey, item -> item, (a, b) -> a));

            for (IndexInfo indexInfo : targetIndices) {
                OpenApiItemDto item = itemsByIndexKey.get(indexKey(indexInfo));

                if (item == null) {
                    continue;
                }

                try {
                    syncJobs.add(indexDataSyncExecutor.syncOne(indexInfo, targetDate, item, worker));
                } catch (Exception e) {
                    syncJobs.add(indexDataSyncExecutor.saveFailure(indexInfo, targetDate, worker));
                }
            }
        }

        return syncJobMapper.toDtoList(syncJobs);
    }

    @Override
    public CursorPageResponseSyncJobDto searchSyncJobs(SyncJobSearchCondition condition) {

        List<SyncJob> fetched = syncJobRepository.search(condition, condition.size() + 1);

        boolean hasNext = fetched.size() > condition.size();
        List<SyncJob> content = hasNext ? fetched.subList(0, condition.size()) : fetched;

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !content.isEmpty()) {
            SyncJob last = content.get(content.size() - 1);
            nextCursor = condition.isSortByTargetDate()
                    ? last.getTargetDate().toString()
                    : last.getJobTime().toString();
            nextIdAfter = last.getId();
        }

        long totalElements = syncJobRepository.count(condition);

        return new CursorPageResponseSyncJobDto(
                syncJobMapper.toDtoList(content),
                nextCursor,
                nextIdAfter,
                condition.size(),
                totalElements,
                hasNext
        );
    }

    private List<IndexInfo> resolveTargetIndices(List<Long> indexInfoIds) {
        if (indexInfoIds == null || indexInfoIds.isEmpty()) {
            return indexInfoRepository.findAll();
        }
        return indexInfoRepository.findAllById(indexInfoIds);
    }

    private String indexKey(IndexInfo indexInfo) {
        return indexInfo.getIndexClassification() + "|" + indexInfo.getIndexName();
    }

    private String indexKey(OpenApiItemDto item) {
        return item.idxCsf() + "|" + item.idxNm();
    }
}

package com.findex.team02.sync.service;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.sync.dto.response.OpenApiItemDto;
import com.findex.team02.sync.entity.SyncJob;
import com.findex.team02.sync.entity.SyncJobResult;
import com.findex.team02.sync.entity.SyncJobType;
import com.findex.team02.sync.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class IndexDataSyncExecutor {

    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob syncOne(IndexInfo indexInfo, LocalDate targetDate, OpenApiItemDto item, String worker) {
        saveOrUpdateIndexData(indexInfo, targetDate, item);
        return saveSyncJob(indexInfo, targetDate, worker, SyncJobResult.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob saveFailure(IndexInfo indexInfo, LocalDate targetDate, String worker) {
        return saveSyncJob(indexInfo, targetDate, worker, SyncJobResult.FAILED);
    }

    private void saveOrUpdateIndexData(IndexInfo indexInfo, LocalDate targetDate, OpenApiItemDto item) {
        indexDataRepository
                .findByIndexInfoAndBaseDate(indexInfo, targetDate)
                .map(indexData -> updateIndexData(indexData, item))
                .orElseGet(() -> createIndexData(indexInfo, targetDate, item));
    }

    private IndexData updateIndexData(IndexData indexData, OpenApiItemDto item) {
        indexData.update(
                item.mkp(),
                item.clpr(),
                item.hipr(),
                item.lopr(),
                item.vs(),
                item.fltRt(),
                item.tvol(),
                item.tamt(),
                item.mktcap()
        );
        return indexData;
    }

    private IndexData createIndexData(IndexInfo indexInfo, LocalDate targetDate, OpenApiItemDto item) {
        IndexData indexData = IndexData.builder()
                .indexInfo(indexInfo)
                .baseDate(targetDate)
                .marketPrice(item.mkp())
                .closingPrice(item.clpr())
                .highPrice(item.hipr())
                .lowPrice(item.lopr())
                .versus(item.vs())
                .fluctuationRate(item.fltRt())
                .tradingQuantity(item.tvol())
                .tradingPrice(item.tamt())
                .marketTotalAmount(item.mktcap())
                .build();

        return indexDataRepository.save(indexData);
    }

    private SyncJob saveSyncJob(IndexInfo indexInfo, LocalDate targetDate, String worker, SyncJobResult result) {
        return syncJobRepository.save(
                SyncJob.builder()
                        .indexInfo(indexInfo)
                        .targetDate(targetDate)
                        .worker(worker)
                        .jobType(SyncJobType.INDEX_DATA)
                        .result(result)
                        .jobTime(LocalDateTime.now())
                        .build()
        );
    }
}

package com.findex.team02.sync.service;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.global.type.SourceType;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class IndexDataSyncExecutor {

    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob syncOne(IndexInfo indexInfo, LocalDate targetDate, OpenApiItemDto item, IndexData existing, String worker) {
        if (existing != null) {
            updateIndexData(existing, item);
        } else {
            createIndexData(indexInfo, targetDate, item);
        }
        return saveSyncJob(indexInfo, targetDate, worker, SyncJobResult.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob saveFailure(IndexInfo indexInfo, LocalDate targetDate, String worker) {
        return saveSyncJob(indexInfo, targetDate, worker, SyncJobResult.FAILED);
    }

    private IndexData updateIndexData(IndexData indexData, OpenApiItemDto item) {
        indexData.update(
                toBigDecimal(item.mkp()),
                toBigDecimal(item.clpr()),
                toBigDecimal(item.hipr()),
                toBigDecimal(item.lopr()),
                toBigDecimal(item.vs()),
                toBigDecimal(item.fltRt()),
                toLong(item.trqu()),
                toLong(item.trPrc()),
                toLong(item.lstgMrktTotAmt())
        );

        return indexDataRepository.save(indexData);
    }

    private IndexData createIndexData(IndexInfo indexInfo, LocalDate targetDate, OpenApiItemDto item) {
        IndexData indexData = IndexData.builder()
                .indexInfo(indexInfo)
                .baseDate(targetDate)
                .sourceType(SourceType.OPEN_API)
                .marketPrice(toBigDecimal(item.mkp()))
                .closingPrice(toBigDecimal(item.clpr()))
                .highPrice(toBigDecimal(item.hipr()))
                .lowPrice(toBigDecimal(item.lopr()))
                .versus(toBigDecimal(item.vs()))
                .fluctuationRate(toBigDecimal(item.fltRt()))
                .tradingQuantity(toLong(item.trqu()))
                .tradingPrice(toLong(item.trPrc()))
                .marketTotalAmount(toLong(item.lstgMrktTotAmt()))
                .build();

        return indexDataRepository.save(indexData);
    }

    private BigDecimal toBigDecimal(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }

    private Long toLong(String value) {
        return (value == null || value.isBlank()) ? null : Long.parseLong(value);
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

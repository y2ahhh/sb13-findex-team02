package com.findex.team02.sync.service;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.entity.SourceType;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
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
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class IndexInfoSyncExecutor {

    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob syncOne(OpenApiItemDto item, String worker) {
        IndexInfo indexInfo = saveOrUpdateIndexInfo(item);
        return saveSyncJob(indexInfo, worker, SyncJobResult.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob saveFailure(String worker) {
        return saveSyncJob(null, worker, SyncJobResult.FAILED);
    }

    private IndexInfo saveOrUpdateIndexInfo(OpenApiItemDto item) {
        return indexInfoRepository
                .findByIndexClassificationAndIndexName(item.idxCsf(), item.idxNm())
                .map(indexInfo -> updateIndexInfo(indexInfo, item))
                .orElseGet(() -> createIndexInfo(item));
    }

    private IndexInfo updateIndexInfo(IndexInfo indexInfo, OpenApiItemDto item) {
        indexInfo.updateInfo(
                toInteger(item.epyItmsCnt()),
                toLocalDate(item.basPntm()),
                toBigDecimal(item.basIdx()),
                indexInfo.getFavorite()
        );
        return indexInfo;
    }

    private IndexInfo createIndexInfo(OpenApiItemDto item) {
        IndexInfo indexInfo = IndexInfo.builder()
                .indexClassification(item.idxCsf())
                .indexName(item.idxNm())
                .employedItemsCount(toInteger(item.epyItmsCnt()))
                .basePointInTime(toLocalDate(item.basPntm()))
                .baseIndex(toBigDecimal(item.basIdx()))
                .favorite(false)
                .sourceType(SourceType.OPEN_API)
                .build();

        return indexInfoRepository.save(indexInfo);
    }

    private Integer toInteger(String value) {
        return (value == null || value.isBlank()) ? null : Integer.parseInt(value);
    }

    private BigDecimal toBigDecimal(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }

    private LocalDate toLocalDate(String value) {
        return (value == null || value.isBlank()) ? null : LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE);
    }

    private SyncJob saveSyncJob(IndexInfo indexInfo, String worker, SyncJobResult result) {
        return syncJobRepository.save(
                SyncJob.builder()
                        .indexInfo(indexInfo)
                        .worker(worker)
                        .jobType(SyncJobType.INDEX_INFO)
                        .result(result)
                        .jobTime(LocalDateTime.now())
                        .build()
        );
    }
}

package com.findex.team02.sync.service;

import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.findex.team02.autosync.repository.AutoSyncConfigRepository;
import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import com.findex.team02.sync.dto.response.OpenApiItemDto;
import com.findex.team02.sync.entity.SyncJob;
import com.findex.team02.sync.entity.SyncJobResult;
import com.findex.team02.sync.entity.SyncJobType;
import com.findex.team02.sync.repository.SyncJobRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class IndexInfoSyncExecutor {

    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob syncOne(OpenApiItemDto item, IndexInfo existing, String worker) {
        IndexInfo indexInfo = (existing != null)
                ? updateIndexInfo(existing, item)
                : createIndexInfo(item);
        return saveSyncJob(indexInfo, worker, SyncJobResult.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob saveFailure(String worker) {
        return saveSyncJob(null, worker, SyncJobResult.FAILED);
    }

    private IndexInfo updateIndexInfo(IndexInfo indexInfo, OpenApiItemDto item) {
        indexInfo.updateInfo(
                toInteger(item.epyItmsCnt()),
                toLocalDate(item.basPntm()),
                toBigDecimal(item.basIdx()),
                indexInfo.getFavorite()
        );

        return indexInfoRepository.save(indexInfo);
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

        IndexInfo saved = indexInfoRepository.save(indexInfo);
        AutoSyncConfig autoSyncConfig = new AutoSyncConfig(saved, false);
        autoSyncConfigRepository.save(autoSyncConfig);
        return saved;
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

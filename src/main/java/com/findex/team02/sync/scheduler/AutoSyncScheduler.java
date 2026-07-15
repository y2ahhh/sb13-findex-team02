package com.findex.team02.sync.scheduler;

import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.findex.team02.autosync.repository.AutoSyncConfigRepository;
import com.findex.team02.sync.dto.request.IndexDataSyncRequest;
import com.findex.team02.sync.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private static final String SYSTEM_WORKER_NAME = "system";

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final SyncJobService syncJobService;

    //현재 테스트 개념으로 매분으로 설정. 최종 구동 시 바꿀 예정.
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void syncIndexDataAutomatically() {
        List<AutoSyncConfig> configs = autoSyncConfigRepository.findAllByEnabledTrue();

        if (configs.isEmpty()) {
            return;
        }

        List<Long> indexInfoIds = configs.stream()
                .map(config -> config.getIndexInfo().getId())
                .toList();

        LocalDate targetDate = LocalDate.now().minusDays(1);

        IndexDataSyncRequest request = new IndexDataSyncRequest(
                indexInfoIds,
                targetDate,
                targetDate
        );

        syncJobService.syncIndexData(request, SYSTEM_WORKER_NAME);
    }
}

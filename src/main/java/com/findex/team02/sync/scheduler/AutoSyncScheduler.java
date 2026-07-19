package com.findex.team02.sync.scheduler;

import com.findex.team02.sync.dto.request.IndexDataSyncRequest;
import com.findex.team02.sync.service.OpenApiService;
import com.findex.team02.sync.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private static final String SYSTEM_WORKER_NAME = "system";
    private final OpenApiService openApiService;

    // 단일 서버 환경에서 이전 자동 연동이 끝나기 전에 다음 스케줄이 실행되는 것을 막는 취지.
    // 다중 서버 환경에서는 JVM 내부 상태를 공유할 수 없으므로 DB Lock 또는 ShedLock이 필요.
    private static final AtomicBoolean running = new AtomicBoolean(false);

    // AutoSyncConfig 조회를 Repo에서 호출하는 것보다 자동 배치에 필요한 대상 지수 ID만 조회하도록 전용 컴포넌트로 분리함.
    private final AutoSyncTargetReader autoSyncTargetReader;
    private final SyncJobService syncJobService;

    //현재 테스트 개념으로 매분으로 설정. 최종 구동 시 바꿀 예정.
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void syncIndexDataAutomatically() {

        if (!running.compareAndSet(false, true)) {
            return;
        }

        try {
            List<Long> indexInfoIds = autoSyncTargetReader.findEnabledIndexInfoIds();
            if (indexInfoIds.isEmpty()) {
                return;
            }

            LocalDate targetDate = openApiService.findLatestAvailableDate();

            IndexDataSyncRequest request = new IndexDataSyncRequest(
                    indexInfoIds,
                    targetDate,
                    targetDate
            );

            syncJobService.syncIndexData(request, SYSTEM_WORKER_NAME);
        } finally {
            running.set(false);
        }
    }
}

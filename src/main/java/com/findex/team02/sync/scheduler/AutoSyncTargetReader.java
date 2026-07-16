package com.findex.team02.sync.scheduler;

import com.findex.team02.autosync.entity.QAutoSyncConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoSyncTargetReader {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<Long> findEnabledIndexInfoIds() {
        QAutoSyncConfig autoSyncConfig = QAutoSyncConfig.autoSyncConfig;

        return queryFactory
                .select(autoSyncConfig.indexInfo.id)
                .from(autoSyncConfig)
                .where(autoSyncConfig.enabled.isTrue())
                .fetch();
    }
}

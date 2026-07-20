package com.findex.team02.sync.repository;

import com.findex.team02.sync.dto.response.SyncJobSearchCondition;
import com.findex.team02.sync.entity.QSyncJob;
import com.findex.team02.sync.entity.SyncJob;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SyncJobRepositoryCustomImpl implements SyncJobRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SyncJob> search(SyncJobSearchCondition condition, int limit) {
        QSyncJob syncJob = QSyncJob.syncJob;

        BooleanBuilder filter = buildFilter(condition, syncJob);
        BooleanExpression cursorCondition = buildCursorCondition(condition, syncJob);
        if (cursorCondition != null) {
            filter.and(cursorCondition);
        }

        OrderSpecifier<?> primaryOrder = resolvePrimaryOrder(condition, syncJob);
        OrderSpecifier<Long> secondaryOrder = condition.isDescending() ? syncJob.id.desc() : syncJob.id.asc();

        return queryFactory
                .selectFrom(syncJob)
                .where(filter)
                .orderBy(primaryOrder, secondaryOrder)
                .limit(limit)
                .fetch();
    }

    @Override
    public long count(SyncJobSearchCondition condition) {
        QSyncJob syncJob = QSyncJob.syncJob;
        BooleanBuilder filter = buildFilter(condition, syncJob);

        Long totalElements = queryFactory
                .select(syncJob.count())
                .from(syncJob)
                .where(filter)
                .fetchOne();

        return totalElements == null ? 0L : totalElements;
    }

    private BooleanBuilder buildFilter(SyncJobSearchCondition condition, QSyncJob syncJob) {
        BooleanBuilder filter = new BooleanBuilder();

        if (condition.jobType() != null) {
            filter.and(syncJob.jobType.eq(condition.jobType()));
        }

        if (condition.indexInfoId() != null) {
            filter.and(syncJob.indexInfo.id.eq(condition.indexInfoId()));
        }

        if (condition.worker() != null && !condition.worker().isBlank()) {
            filter.and(syncJob.worker.eq(condition.worker()));
        }

        if (condition.status() != null) {
            filter.and(syncJob.result.eq(condition.status()));
        }

        if (condition.baseDateFrom() != null) {
            filter.and(syncJob.targetDate.goe(condition.baseDateFrom()));
        }

        if (condition.baseDateTo() != null) {
            filter.and(syncJob.targetDate.loe(condition.baseDateTo()));
        }

        if (condition.jobTimeFrom() != null) {
            filter.and(syncJob.jobTime.goe(condition.jobTimeFrom()));
        }

        if (condition.jobTimeTo() != null) {
            filter.and(syncJob.jobTime.loe(condition.jobTimeTo()));
        }

        if (condition.isSortByTargetDate()) {
            filter.and(syncJob.targetDate.isNotNull());
        }

        return filter;
    }

    private OrderSpecifier<?> resolvePrimaryOrder(SyncJobSearchCondition condition, QSyncJob syncJob) {
        boolean descending = condition.isDescending();
        if (condition.isSortByTargetDate()) {
            return descending ? syncJob.targetDate.desc() : syncJob.targetDate.asc();
        }
        return descending ? syncJob.jobTime.desc() : syncJob.jobTime.asc();
    }

    private BooleanExpression buildCursorCondition(SyncJobSearchCondition condition, QSyncJob syncJob) {
        if (condition.cursor() == null || condition.idAfter() == null) {
            return null;
        }

        boolean descending = condition.isDescending();
        Long idAfter = condition.idAfter();

        if (condition.isSortByTargetDate()) {
            LocalDate cursorTargetDate = LocalDate.parse(condition.cursor());
            return descending
                    ? syncJob.targetDate.lt(cursorTargetDate)
                    .or(syncJob.targetDate.eq(cursorTargetDate).and(syncJob.id.lt(idAfter)))
                    : syncJob.targetDate.gt(cursorTargetDate)
                    .or(syncJob.targetDate.eq(cursorTargetDate).and(syncJob.id.gt(idAfter)));
        }

        LocalDateTime cursorJobTime = LocalDateTime.parse(condition.cursor());
        return descending
                ? syncJob.jobTime.lt(cursorJobTime)
                .or(syncJob.jobTime.eq(cursorJobTime).and(syncJob.id.lt(idAfter)))
                : syncJob.jobTime.gt(cursorJobTime)
                .or(syncJob.jobTime.eq(cursorJobTime).and(syncJob.id.gt(idAfter)));
    }
}

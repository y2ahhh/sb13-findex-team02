package com.findex.team02.autosync.repository;

import com.findex.team02.autosync.dto.request.AutoSyncConfigSearchRequest;
import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.findex.team02.autosync.entity.QAutoSyncConfig.autoSyncConfig;
import static com.findex.team02.indexinfo.entity.QIndexInfo.indexInfo;

@RequiredArgsConstructor
public class AutoSyncConfigRepositoryImpl implements AutoSyncConfigRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countTotalElements(AutoSyncConfigSearchRequest request) {
        Long count = queryFactory
                .select(autoSyncConfig.count())
                .from(autoSyncConfig)
                .where(
                        indexInfoIdEq(request.indexInfoId()),
                        enabledEq(request.enabled())
                )
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<AutoSyncConfig> findAllByCondition(AutoSyncConfigSearchRequest request) {

        return queryFactory
                .selectFrom(autoSyncConfig)
                .where(
                        indexInfoIdEq(request.indexInfoId()),
                        enabledEq(request.enabled()),
                        cursorCondition(
                                request.sortField(),
                                request.sortDirection(),
                                request.cursor(),
                                request.idAfter()
                        )
                )
                .orderBy(orderSpecifiers(
                        request.sortField(),
                        request.sortDirection()
                ))
                .limit(request.size() + 1)
                .fetch();

    }

    private OrderSpecifier<?>[] orderSpecifiers(String sortField, String sortDirection) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (!StringUtils.hasText(sortField)) {
            sortField = "indexInfo.indexName";
        }

        Order direction = "desc".equalsIgnoreCase(sortDirection)
                ? Order.DESC
                : Order.ASC;

        if (sortField.equals("enabled")) {
            orderSpecifiers.add(new OrderSpecifier<>(direction, autoSyncConfig.enabled));
        } else {
            orderSpecifiers.add(new OrderSpecifier<>(direction, autoSyncConfig.indexInfo.indexName));
        }

        // 커서 페이지네이션 안정성을 위한 동일 방향 정렬
        orderSpecifiers.add(new OrderSpecifier<>(direction, autoSyncConfig.id));

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression indexInfoIdEq(Long indexInfoId) {
        return indexInfoId != null
                ? autoSyncConfig.indexInfo.id.eq(indexInfoId)
                : null;
    }

    private BooleanExpression enabledEq(Boolean enabled) {
        return enabled != null
                ? autoSyncConfig.enabled.eq(enabled)
                : null;
    }

    private BooleanExpression cursorCondition(
            String sortField,
            String sortDirection,
            String cursor,
            Long idAfter
    ) {
        if (!StringUtils.hasText(cursor) || idAfter == null) {
            return null; // 첫 페이지
        }

        boolean desc = "desc".equalsIgnoreCase(sortDirection);

        if ("enabled".equals(sortField)) {
            return enabledCursorCondition(Boolean.parseBoolean(cursor), idAfter, desc);
        }

        return desc
                ? autoSyncConfig.indexInfo.indexName.lt(cursor)
                .or(autoSyncConfig.indexInfo.indexName.eq(cursor)
                        .and(autoSyncConfig.id.lt(idAfter)))
                : autoSyncConfig.indexInfo.indexName.gt(cursor)
                .or(autoSyncConfig.indexInfo.indexName.eq(cursor)
                        .and(autoSyncConfig.id.gt(idAfter)));
    }

    private BooleanExpression enabledCursorCondition(
            boolean cursorEnabled,
            Long cursorId,
            boolean desc
    ) {
        BooleanExpression sameEnabled = autoSyncConfig.enabled.eq(cursorEnabled)
                .and(desc
                        ? autoSyncConfig.id.lt(cursorId)
                        : autoSyncConfig.id.gt(cursorId));

        BooleanExpression nextEnabled = desc
                ? autoSyncConfig.enabled.eq(false).and(autoSyncConfig.enabled.ne(cursorEnabled))
                : autoSyncConfig.enabled.eq(true).and(autoSyncConfig.enabled.ne(cursorEnabled));

        return sameEnabled.or(nextEnabled);
    }
}

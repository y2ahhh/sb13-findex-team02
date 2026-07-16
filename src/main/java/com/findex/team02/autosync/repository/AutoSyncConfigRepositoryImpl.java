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
                        indexInfoIdEq(request.indexInfoId())
                )
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<AutoSyncConfig> findAllByCondition(AutoSyncConfigSearchRequest request) {

        return queryFactory
                .selectFrom(autoSyncConfig)
                .where(
                        indexInfoIdEq(request.indexInfoId())
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
}

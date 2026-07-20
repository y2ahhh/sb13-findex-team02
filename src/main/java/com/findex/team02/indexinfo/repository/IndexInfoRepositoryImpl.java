package com.findex.team02.indexinfo.repository;

import static com.findex.team02.indexinfo.entity.QIndexInfo.indexInfo;

import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class IndexInfoRepositoryImpl implements IndexInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public long countTotalElements(IndexInfoSearchRequest request) {
        Long count = queryFactory
                .select(indexInfo.count())
                .from(indexInfo)
                .where(
                        indexClassificationContains(request.indexClassification()),
                        indexNameContains(request.indexName()),
                        favoriteEq(request.favorite())
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexInfo> findAllByCondition(IndexInfoSearchRequest request) {

        return queryFactory
                .selectFrom(indexInfo)
                .where(
                        indexClassificationContains(request.indexClassification()),
                        indexNameContains(request.indexName()),
                        favoriteEq(request.favorite()),
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

    // 동적 정렬 메서드
    private OrderSpecifier<?>[] orderSpecifiers(String sortField, String sortDirection) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (!StringUtils.hasText(sortField)) {
            sortField = "indexClassification";
        }

        Order direction = "desc".equalsIgnoreCase(sortDirection)
                ? Order.DESC
                : Order.ASC;

        switch (sortField) {
            case "indexName":
                orderSpecifiers.add(new OrderSpecifier<>(direction, indexInfo.indexName));
                break;
            case "employedItemsCount":
                orderSpecifiers.add(new OrderSpecifier<>(direction, indexInfo.employedItemsCount));
                break;
            default:
                orderSpecifiers.add(new OrderSpecifier<>(direction, indexInfo.indexClassification));
        }

        // 커서 페이지네이션 안정성을 위한 동일 방향 정렬
        orderSpecifiers.add(new OrderSpecifier<>(direction, indexInfo.id));

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    // 지수 분류명 필터
    private BooleanExpression indexClassificationContains(String classification) {
        return StringUtils.hasText(classification)
                ? indexInfo.indexClassification.containsIgnoreCase(classification)
                : null;
    }

    // 지수명 필터
    private BooleanExpression indexNameContains(String name) {
        return StringUtils.hasText(name)
                ? indexInfo.indexName.containsIgnoreCase(name)
                : null;
    }

    // 즐겨찾기 필터
    private BooleanExpression favoriteEq(Boolean favorite) {
        return favorite != null
                ? indexInfo.favorite.eq(favorite)
                : null;
    }

    // 커서 페이지 네이션
    private BooleanExpression cursorCondition(
            String sortField,
            String sortDirection,
            String cursor,
            Long idAfter
    ) {
        if (!StringUtils.hasText(cursor) || idAfter == null) {
            return null;
        }

        if (!StringUtils.hasText(sortField)) {
            sortField = "indexClassification";
        }

        boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

        BooleanExpression idCondition = isDesc
                ? indexInfo.id.lt(idAfter)
                : indexInfo.id.gt(idAfter);

        return switch (sortField) {
            case "indexName" -> isDesc
                    ? indexInfo.indexName.lt(cursor)
                    .or(indexInfo.indexName.eq(cursor)
                            .and(idCondition))
                    : indexInfo.indexName.gt(cursor)
                    .or(indexInfo.indexName.eq(cursor)
                            .and(idCondition));
            case "employedItemsCount" -> {
                Integer count = Integer.valueOf(cursor);

                yield isDesc
                        ? indexInfo.employedItemsCount.lt(count)
                        .or(indexInfo.employedItemsCount.eq(count)
                                .and(idCondition))
                        : indexInfo.employedItemsCount.gt(count)
                        .or(indexInfo.employedItemsCount.eq(count)
                                .and(idCondition));
            }
            default -> isDesc
                    ? indexInfo.indexClassification.lt(cursor)
                    .or(indexInfo.indexClassification.eq(cursor)
                            .and(idCondition))
                    : indexInfo.indexClassification.gt(cursor)
                    .or(indexInfo.indexClassification.eq(cursor)
                            .and(idCondition));
        };

    }

}

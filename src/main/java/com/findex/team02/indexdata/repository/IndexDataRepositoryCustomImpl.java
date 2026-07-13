package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexDataRepositoryCustomImpl implements IndexDataRepositoryCustom {

  private final EntityManager entityManager;

  @Override
  public List<IndexData> findCursorPage(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size
  ) {

    validateSortField(sortField);
    validateSortDirection(sortDirection);

    StringBuilder jpql = new StringBuilder("""
        SELECT d
        FROM IndexData d
        WHERE 1 = 1
        """);

    if (indexInfoId != null) {
      jpql.append(" AND d.indexInfo.id = :indexInfoId");
    }

    if (startDate != null) {
      jpql.append(" AND d.baseDate >= :startDate");
    }

    if (endDate != null) {
      jpql.append(" AND d.baseDate <= :endDate");
    }

    Object cursorValue = null;

    if (cursor != null && idAfter != null) {
      cursorValue = parseCursor(cursor, sortField);

      String operator = sortDirection.equalsIgnoreCase("asc") ? ">" : "<";

      jpql.append("""
          And (
            d.%s %s :cursorValue
            OR (d.%s = :cursorValue AND d.id %s :idAfter)
            )
          """.formatted(sortField, operator, sortField, operator
      ));
    }

    jpql.append(
        " ORDER BY d.%s %s, d.id %s"
            .formatted(sortField, sortDirection, sortDirection));

    TypedQuery<IndexData> query = entityManager.createQuery(jpql.toString(), IndexData.class);

    if (indexInfoId != null) {
      query.setParameter("indexInfoId", indexInfoId);
    }

    if (startDate != null) {
      query.setParameter("startDate", startDate);
    }

    if (endDate != null) {
      query.setParameter("endDate", endDate);
    }

    if (cursorValue != null) {
      query.setParameter("cursorValue", cursorValue);
      query.setParameter("idAfter", idAfter);
    }

    query.setMaxResults(size + 1);

    return query.getResultList();
  }

  @Override
  public long countByCondition(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate
  ) {

    StringBuilder jpql = new StringBuilder("""
        SELECT COUNT(d)
        FROM IndexData d
        WHERE 1 = 1
        """);

    if (indexInfoId != null) {
      jpql.append(" AND d.indexInfo.id = :indexInfoId");
    }

    if (startDate != null) {
      jpql.append(" AND d.baseDate >= :startDate");
    }

    if (endDate != null) {
      jpql.append(" AND d.baseDate <= :endDate");
    }

    TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

    if (indexInfoId != null) {
      query.setParameter("indexInfoId", indexInfoId);
    }

    if (startDate != null) {
      query.setParameter("startDate", startDate);
    }

    if (endDate != null) {
      query.setParameter("endDate", endDate);
    }

    return query.getSingleResult();
  }

  private void validateSortField(String sortField) {
    List<String> allowedFields = List.of(
        "baseDate",
        "marketPrice",
        "closingPrice",
        "highPrice",
        "lowPrice",
        "versus",
        "fluctuationRate",
        "tradingQuantity",
        "tradingPrice",
        "marketTotalAmount"
    );

    if (!allowedFields.contains(sortField)) {
      throw new IllegalArgumentException("Invalid sort field: " + sortField);
    }
  }

  private void validateSortDirection(String sortDirection) {
    if (!sortDirection.equalsIgnoreCase("asc")
        && !sortDirection.equalsIgnoreCase("desc")) {

      throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
    }
  }

  private Object parseCursor(String cursor, String sortField) {
    return switch (sortField) {
      case "baseDate" -> LocalDate.parse(cursor);

      case "marketPrice",
           "closingPrice",
           "highPrice",
           "lowPrice",
           "versus",
           "fluctuationRate" ->
        new BigDecimal(cursor);

      case "tradingQuantity",
           "tradingPrice",
           "marketTotalAmount" ->
        Long.parseLong(cursor);

      default -> throw new IllegalArgumentException("Invalid sort field: " + sortField);
    };

  }

}

package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.List;

public interface IndexDataRepositoryCustom {

  List<IndexData> findCursorPage(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size
  );

  long countByCondition(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate
  );
}

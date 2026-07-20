package com.findex.team02.indexdata.service;

import com.findex.team02.indexdata.dto.request.IndexDataCreateRequest;
import com.findex.team02.indexdata.dto.response.CursorPageResponseIndexDataDto;
import com.findex.team02.indexdata.dto.response.IndexDataDto;
import com.findex.team02.indexdata.dto.request.IndexDataUpdateRequest;
import java.time.LocalDate;

public interface IndexDataService {

  IndexDataDto create(IndexDataCreateRequest request);

  IndexDataDto update(Long id, IndexDataUpdateRequest request);

  void delete(Long id);

  IndexDataDto findById(Long id);

  CursorPageResponseIndexDataDto findAll(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      Integer size
  );

}

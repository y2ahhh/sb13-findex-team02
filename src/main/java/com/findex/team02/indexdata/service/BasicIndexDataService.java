package com.findex.team02.indexdata.service;

import com.findex.team02.global.exception.ResourceNotFoundException;
import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexdata.dto.request.IndexDataCreateRequest;
import com.findex.team02.indexdata.dto.request.IndexDataUpdateRequest;
import com.findex.team02.indexdata.dto.response.CursorPageResponseIndexDataDto;
import com.findex.team02.indexdata.dto.response.IndexDataDto;
import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexdata.mapper.IndexDataMapper;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicIndexDataService implements IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataMapper indexDataMapper;

  @Override
  public IndexDataDto create(IndexDataCreateRequest request) {

    IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new ResourceNotFoundException("지수 정보를 찾을 수 없습니다."));

    boolean exists = indexDataRepository.existsByIndexInfo_IdAndBaseDate(
        request.indexInfoId(),
        request.baseDate()
    );

    if (exists) {
      throw new IllegalArgumentException("해당 지수의 기준일 데이터가 이미 존재합니다.");
    }

    IndexData indexData = new IndexData(
        indexInfo,
        request.baseDate(),
        SourceType.USER,
        request.marketPrice(),
        request.closingPrice(),
        request.highPrice(),
        request.lowPrice(),
        request.versus(),
        request.fluctuationRate(),
        request.tradingQuantity(),
        request.tradingPrice(),
        request.marketTotalAmount()
    );

    IndexData saved = indexDataRepository.save(indexData);

    return indexDataMapper.toDto(saved);
  }

  @Override
  public IndexDataDto update(Long id, IndexDataUpdateRequest request) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("지수 데이터를 찾을 수 없습니다."));

    indexData.update(
        request.marketPrice(),
        request.closingPrice(),
        request.highPrice(),
        request.lowPrice(),
        request.versus(),
        request.fluctuationRate(),
        request.tradingQuantity(),
        request.tradingPrice(),
        request.marketTotalAmount()
    );

    return indexDataMapper.toDto(indexData);
  }

  @Override
  public void delete(Long id) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("지수 데이터를 찾을 수 없습니다."));

    indexDataRepository.delete(indexData);
  }

  @Transactional(readOnly = true)
  @Override
  public IndexDataDto findById(Long id) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("지수 데이터를 찾을 수 없습니다."));

    return indexDataMapper.toDto(indexData);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseIndexDataDto findAll(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      Integer size
  ) {

    validatePageRequest(
        startDate,
        endDate,
        idAfter,
        cursor,
        size
    );

    List<IndexData> indexDataList = new ArrayList<>(
        indexDataRepository.findCursorPage(
            indexInfoId,
            startDate,
            endDate,
            idAfter,
            cursor,
            sortField,
            sortDirection,
            size
        )
    );

    boolean hasNext = indexDataList.size() > size;

    if (hasNext) {
      indexDataList.remove(indexDataList.size() - 1);
    }

    List<IndexDataDto> content = indexDataList.stream()
        .map(indexDataMapper::toDto)
        .toList();

    String nextCursor = null;
    Long nextIdAfter = null;

    if (hasNext && !indexDataList.isEmpty()) {
      IndexData lastIndexData = indexDataList.get(indexDataList.size() - 1);

      nextCursor = createCursor(lastIndexData, sortField);
      nextIdAfter = lastIndexData.getId();
    }

    long totalElements = indexDataRepository.countByCondition(
        indexInfoId,
        startDate,
        endDate
    );

    return new CursorPageResponseIndexDataDto(
        content,
        nextCursor,
        nextIdAfter,
        size,
        totalElements,
        hasNext
    );
  }

  private String createCursor(IndexData indexData, String sortField) {
    return switch (sortField) {
      case "baseDate" -> indexData.getBaseDate().toString();
      case "marketPrice" -> indexData.getMarketPrice().toPlainString();
      case "closingPrice" -> indexData.getClosingPrice().toPlainString();
      case "highPrice" -> indexData.getHighPrice().toPlainString();
      case "lowPrice" -> indexData.getLowPrice().toPlainString();
      case "versus" -> indexData.getVersus().toPlainString();
      case "fluctuationRate" -> indexData.getFluctuationRate().toPlainString();
      case "tradingQuantity" -> indexData.getTradingQuantity().toString();
      case "tradingPrice" -> indexData.getTradingPrice().toString();
      case "marketTotalAmount" -> indexData.getMarketTotalAmount().toString();
      default -> throw new IllegalArgumentException("Invalid sort field: " + sortField);
    };
  }

  private void validatePageRequest(
      LocalDate startDate,
      LocalDate endDate,
      Long idAfter,
      String cursor,
      Integer size
  ) {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("시작 일자는 종료 일자보다 이후일 수 없습니다.");
    }

    if ((cursor == null) != (idAfter == null)) {
      throw new IllegalArgumentException("cursor와 idAfter는 함께 전달되어야 합니다.");
    }

    if (size == null || size <= 0) {
      throw new IllegalArgumentException("페이지 크기는 1 이상이어야 합니다.");
    }
  }

}

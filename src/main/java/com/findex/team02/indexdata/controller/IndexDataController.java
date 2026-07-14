package com.findex.team02.indexdata.controller;

import com.findex.team02.indexdata.dto.request.IndexDataCreateRequest;
import com.findex.team02.indexdata.dto.request.IndexDataUpdateRequest;
import com.findex.team02.indexdata.dto.response.CursorPageResponseIndexDataDto;
import com.findex.team02.indexdata.dto.response.IndexDataDto;
import com.findex.team02.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

  private final IndexDataService indexDataService;

  @PostMapping
  public ResponseEntity<IndexDataDto> create(
      @RequestBody @Valid IndexDataCreateRequest request
  ) {
    IndexDataDto response = indexDataService.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<IndexDataDto> update(
      @PathVariable Long id,
      @RequestBody @Valid IndexDataUpdateRequest request
  ) {
    IndexDataDto response = indexDataService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable Long id
  ) {
    indexDataService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<IndexDataDto> findById(
      @PathVariable Long id
  ) {
    IndexDataDto response = indexDataService.findById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseIndexDataDto> findAll(
      @RequestParam(required = false) Long indexInfoId,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "baseDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "10") Integer size
  ) {
    CursorPageResponseIndexDataDto response = indexDataService.findAll(
        indexInfoId,
        startDate,
        endDate,
        idAfter,
        cursor,
        sortField,
        sortDirection,
        size
    );

    return ResponseEntity.ok(response);
  }

}

package com.findex.team02.indexinfo.controller;

import com.findex.team02.indexinfo.dto.request.IndexInfoCreateRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoSummaryDto;
import com.findex.team02.indexinfo.service.IndexInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    // ─── 지수 정보 등록 (POST /api/index-infos) ─────────────────
    @PostMapping
    public ResponseEntity<IndexInfoDto> createIndexInfo(
        @RequestBody @Valid IndexInfoCreateRequest request) {
        // @Valid: request 안에 선언한 @NotBlank, @NotNull 유효성 검사를 실행합니다.
        IndexInfoDto result = indexInfoService.createIndexInfo(request);
        // 생성 성공 → HTTP 201 Created 반환 (200 OK가 아닙니다!)
        return ResponseEntity.created(null).body(result);
    }
    // ─── 지수 정보 수정 (PATCH /api/index-infos/{id}) ───────────
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> updateIndexInfo(
        @PathVariable Long id,
        @RequestBody @Valid IndexInfoUpdateRequest request) {
        IndexInfoDto result = indexInfoService.updateIndexInfo(id, request);
        return ResponseEntity.ok(result);
    }
    // ─── 지수 정보 삭제 (DELETE /api/index-infos/{id}) ──────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndexInfo(
        @PathVariable Long id) {
        indexInfoService.deleteIndexInfo(id);
        // 삭제 성공 → HTTP 204 No Content 반환 (응답 바디가 없을 때)
        return ResponseEntity.noContent().build();
    }

    // 지수 정보 목록 조회
    @GetMapping
    public ResponseEntity<CursorPageResponseIndexInfoDto> getIndexInfos(@ModelAttribute IndexInfoSearchRequest request) {
        CursorPageResponseIndexInfoDto response = indexInfoService.getIndexInfos(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> getSummaries() {
        List<IndexInfoSummaryDto> response = indexInfoService.getIndexInfoSummary();

        return ResponseEntity.ok(response);
    }
    // ─── 지수 정보 단건 조회 (GET /api/index-infos/{id}) ────────
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> getIndexInfo(
        @PathVariable Long id) {
        IndexInfoDto result = indexInfoService.getIndexInfo(id);
        return ResponseEntity.ok(result);
    }
}

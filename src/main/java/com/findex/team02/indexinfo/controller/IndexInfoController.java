package com.findex.team02.indexinfo.controller;

import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoSummaryDto;
import com.findex.team02.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

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

}

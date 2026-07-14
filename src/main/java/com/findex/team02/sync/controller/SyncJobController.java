package com.findex.team02.sync.controller;

import com.findex.team02.sync.dto.request.IndexDataSyncRequest;
import com.findex.team02.sync.dto.response.CursorPageResponseSyncJobDto;
import com.findex.team02.sync.dto.response.SyncJobDto;
import com.findex.team02.sync.dto.response.SyncJobSearchCondition;
import com.findex.team02.sync.service.SyncJobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
@RestController
public class SyncJobController {

    private final SyncJobService syncJobService;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfo(HttpServletRequest request) {
        List<SyncJobDto> indexInfo = syncJobService.syncIndexInfo(resolveWorkerIp(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(indexInfo);
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>> syncIndexData(
            @Valid @RequestBody IndexDataSyncRequest syncRequest,
            HttpServletRequest request
    ) {
        List<SyncJobDto> indexData = syncJobService.syncIndexData(syncRequest, resolveWorkerIp(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(indexData);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJobDto> searchSyncJobs(@ModelAttribute SyncJobSearchCondition condition) {
        return ResponseEntity.ok(syncJobService.searchSyncJobs(condition));
    }

    private String resolveWorkerIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

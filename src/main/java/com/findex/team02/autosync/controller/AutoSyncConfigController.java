package com.findex.team02.autosync.controller;

import com.findex.team02.autosync.dto.request.AutoSyncConfigSearchRequest;
import com.findex.team02.autosync.dto.request.AutoSyncConfigUpdateRequest;
import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.dto.response.CursorPageResponseAutoSyncConfigDto;
import com.findex.team02.autosync.service.AutoSyncConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @GetMapping
    public ResponseEntity<CursorPageResponseAutoSyncConfigDto> findAll(@ModelAttribute AutoSyncConfigSearchRequest request) {
        CursorPageResponseAutoSyncConfigDto response = autoSyncConfigService.findAll(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public AutoSyncConfigDto update(
            @PathVariable Long id,
            @Valid @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        return autoSyncConfigService.update(id, request);
    }
}

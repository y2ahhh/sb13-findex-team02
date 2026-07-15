package com.findex.team02.autosync.controller;

import com.findex.team02.autosync.dto.request.AutoSyncConfigUpdateRequest;
import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.dto.response.CursorPageResponseAutoSyncConfigDto;
import com.findex.team02.autosync.service.AutoSyncConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @GetMapping
    public CursorPageResponseAutoSyncConfigDto findAll(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer size
    ) {
        return autoSyncConfigService.findAll(
                indexInfoId,
                enabled,
                idAfter,
                cursor,
                sortField,
                sortDirection,
                size
        );
    }

    @PatchMapping("/{id}")
    public AutoSyncConfigDto update(
            @PathVariable Long id,
            @Valid @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        return autoSyncConfigService.update(id, request);
    }
}

package com.findex.team02.autosync.dto.request;

import jakarta.validation.constraints.NotNull;

public record AutoSyncConfigUpdateRequest(
        @NotNull Boolean enabled
) {
}

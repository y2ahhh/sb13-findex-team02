package com.findex.team02.autosync.service;

import com.findex.team02.autosync.dto.request.AutoSyncConfigUpdateRequest;
import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.dto.response.CursorPageResponseAutoSyncConfigDto;
import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.findex.team02.autosync.repository.AutoSyncConfigRepository;
import com.findex.team02.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAutoSyncConfigService implements AutoSyncConfigService{

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseAutoSyncConfigDto findAll(Long indexInfoId, Boolean enabled, Long idAfter, String cursor, String sortField, String sortDirection, Integer size) {

        int pageSize = size == null ? 10 : size;

        List<AutoSyncConfig> configs = autoSyncConfigRepository.findAll()
                .stream()
                .filter(config -> indexInfoId == null
                        || config.getIndexInfo().getId().equals(indexInfoId))
                .filter(config -> enabled == null
                        || config.getEnabled().equals(enabled))
                .sorted(resolveComparator(sortField, sortDirection))
                .toList();

        List<AutoSyncConfig> pagedConfigs = configs.stream()
                .filter(config -> idAfter == null || config.getId() > idAfter)
                .limit(pageSize + 1)
                .toList();

        boolean hasNext = pagedConfigs.size() > pageSize;

        List<AutoSyncConfig> content = hasNext
                ? pagedConfigs.subList(0, pageSize)
                : pagedConfigs;

        Long nextIdAfter = content.isEmpty()
                ? null
                : content.get(content.size() - 1).getId();

        return new CursorPageResponseAutoSyncConfigDto(
                content.stream()
                        .map(this::toDto)
                        .toList(),
                null,
                nextIdAfter,
                pageSize,
                configs.size(),
                hasNext
        );
    }

    @Override
    public AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request) {
        AutoSyncConfig config = autoSyncConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("자동 연동 설정을 찾을 수 없습니다."));

        config.updateEnabled(request.enabled());

        return toDto(config);
    }

    private AutoSyncConfigDto toDto(AutoSyncConfig config) {
        return new AutoSyncConfigDto(
                config.getId(),
                config.getIndexInfo().getId(),
                config.getIndexInfo().getIndexName(),
                config.getIndexInfo().getIndexClassification(),
                config.getEnabled()
        );
    }

    private Comparator<AutoSyncConfig> resolveComparator(String sortField, String sortDirection) {
        Comparator<AutoSyncConfig> comparator = switch (sortField == null ? "id" : sortField) {
            case "indexInfoId" -> Comparator.comparing(config -> config.getIndexInfo().getId());
            case "indexName" -> Comparator.comparing(config -> config.getIndexInfo().getIndexName());
            case "indexClassification" -> Comparator.comparing(config -> config.getIndexInfo().getIndexClassification());
            case "enabled" -> Comparator.comparing(AutoSyncConfig::getEnabled);
            default -> Comparator.comparing(AutoSyncConfig::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            return comparator.reversed();
        }

        return comparator;
    }
}


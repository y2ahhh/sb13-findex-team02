package com.findex.team02.autosync.mapper;

import com.findex.team02.autosync.dto.response.AutoSyncConfigDto;
import com.findex.team02.autosync.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutoSyncConfigMapper {

    // 명시적 연관 엔터티 경로 지정
    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    @Mapping(target = "indexName", source = "indexInfo.indexName")
    @Mapping(target = "indexClassification", source = "indexInfo.indexClassification")
    AutoSyncConfigDto toDto(AutoSyncConfig config);

    List<AutoSyncConfigDto> toDto(List<AutoSyncConfig> configs);

}

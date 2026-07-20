package com.findex.team02.sync.mapper;

import com.findex.team02.sync.dto.response.SyncJobDto;
import com.findex.team02.sync.entity.SyncJob;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    SyncJobDto toDto(SyncJob syncJob);

    List<SyncJobDto> toDtoList(List<SyncJob> syncJobs);
}
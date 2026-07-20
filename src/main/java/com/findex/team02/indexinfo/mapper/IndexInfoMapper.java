package com.findex.team02.indexinfo.mapper;

import com.findex.team02.indexinfo.dto.request.IndexInfoCreateRequest;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

    @Mapping(target = "sourceType", ignore = true)
    IndexInfo toEntity(IndexInfoCreateRequest request);//Dto->Entity

    IndexInfoDto toDto(IndexInfo index); //Entity 1개->Dto

    List<IndexInfoDto> toDto(List<IndexInfo> indexInfos); //Entity 여러개->Dto
}

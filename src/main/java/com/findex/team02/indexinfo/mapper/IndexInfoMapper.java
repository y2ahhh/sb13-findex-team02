package com.findex.team02.indexinfo.mapper;

import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

    IndexInfoDto toDto(IndexInfo index);

    List<IndexInfoDto> toDto(List<IndexInfo> indexInfos);

}

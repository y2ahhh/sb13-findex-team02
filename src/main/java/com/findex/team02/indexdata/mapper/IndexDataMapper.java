package com.findex.team02.indexdata.mapper;

import com.findex.team02.indexdata.dto.response.IndexDataDto;
import com.findex.team02.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  @Mapping(target = "indexInfoId", source = "indexInfo.id")
  IndexDataDto toDto(IndexData entity);

}

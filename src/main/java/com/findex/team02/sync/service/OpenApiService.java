package com.findex.team02.sync.service;

import com.findex.team02.sync.dto.response.OpenApiItemDto;

import java.time.LocalDate;
import java.util.List;

public interface OpenApiService {

    List<OpenApiItemDto> getIndexData(LocalDate date);

    LocalDate findLatestAvailableDate();

    List<OpenApiItemDto> getLatestIndexData();
}

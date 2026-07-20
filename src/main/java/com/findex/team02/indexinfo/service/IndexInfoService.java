package com.findex.team02.indexinfo.service;

import com.findex.team02.indexinfo.dto.request.IndexInfoCreateRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoSummaryDto;
import java.util.List;

public interface IndexInfoService {
    // 지수 정보 등록 - 요청 데이터를 받아서 저장 후 응답 데이터 반환
    IndexInfoDto createIndexInfo(IndexInfoCreateRequest request);

    // 지수 정보 수정 - ID와 수정 데이터를 받아서 수정 후 응답 데이터 반환
    IndexInfoDto updateIndexInfo(Long id, IndexInfoUpdateRequest request);

    // 지수 정보 삭제 - ID를 받아서 해당 지수 정보(및 관련 지수 데이터) 삭제
    void deleteIndexInfo(Long id);

    CursorPageResponseIndexInfoDto getIndexInfos(IndexInfoSearchRequest request);

    // 지수 정보 단건 조회 - ID로 조회하여 응답 데이터 반환
    IndexInfoDto getIndexInfo(Long id);
    List<IndexInfoSummaryDto> getIndexInfoSummary();

}

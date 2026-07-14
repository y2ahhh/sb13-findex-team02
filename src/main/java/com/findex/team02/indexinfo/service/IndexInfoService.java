package com.findex.team02.indexinfo.service;

import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;

public interface IndexInfoService {

    CursorPageResponseIndexInfoDto getIndexInfos(IndexInfoSearchRequest request);

}

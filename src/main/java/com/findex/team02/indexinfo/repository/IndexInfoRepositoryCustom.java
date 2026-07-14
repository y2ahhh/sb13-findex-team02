package com.findex.team02.indexinfo.repository;


import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.entity.IndexInfo;

import java.util.List;

public interface IndexInfoRepositoryCustom {

    long countTotalElements(IndexInfoSearchRequest request);

    List<IndexInfo> findAllByCondition(IndexInfoSearchRequest request);

}

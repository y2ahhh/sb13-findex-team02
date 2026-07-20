package com.findex.team02.indexinfo.repository.projection;

// 지수 정보 목록 조회 시 필요한 컬럼만 가져오기 위한 Projection 인터페이스
public interface IndexInfoSummary {
    Long getId();

    String getIndexClassification();

    String getIndexName();
}

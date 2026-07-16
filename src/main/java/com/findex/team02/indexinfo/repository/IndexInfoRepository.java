package com.findex.team02.indexinfo.repository;

import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.repository.projection.IndexInfoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexInfoRepository
        extends JpaRepository<IndexInfo, Long>, IndexInfoRepositoryCustom {

    // 분류명과 지수명으로 지수 정보를 조회한다.
    Optional<IndexInfo> findByIndexClassificationAndIndexName(
            String indexClassification,
            String indexName
    );

    // 분류명과 지수명이 동일한 지수 정보가 존재하는지 확인한다.
    boolean existsByIndexClassificationAndIndexName(
            String indexClassification,
            String indexName
    );

    // 관심 지수로 등록된 모든 지수 정보를 조회한다.
    List<IndexInfo> findAllByFavoriteTrue();

    // 출처 유형을 기준으로 지수 정보를 조회한다.
    List<IndexInfo> findBySourceType(SourceType sourceType);

    // 지수명에 특정 키워드가 포함된 지수 정보를 조회한다.
    List<IndexInfo> findByIndexNameContaining(String keyword);

    // 지수 정보의 특정 컬럼만 Projection으로 조회한다.
    List<IndexInfoSummary> findAllSummaryBy();
}
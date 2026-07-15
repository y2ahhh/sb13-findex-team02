package com.findex.team02.indexinfo.repository;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexinfo.repository.projection.IndexInfoSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, IndexInfoRepositoryCustom {

    // 분류명 + 지수명으로 조회하는 메서드
    Optional<IndexInfo> findByIndexClassificationAndIndexName(
            String indexClassification,
            String indexName
    );

    // 사용하는 곳 없는 메서드 삭제 예정
    boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

    List<IndexInfo> findByFavoriteTrue();

    List<IndexInfo> findBySourceType(SourceType sourceType);

    List<IndexInfo> findByIndexNameContaining(String keyword);

    // 특정 컬럼만 조회 (Projection)
    List<IndexInfoSummary> findAllSummaryBy();

}



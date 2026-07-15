package com.findex.team02.indexinfo.repository;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.global.type.SourceType;
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

    boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

    List<IndexInfo> findByFavoriteTrue();

    List<IndexInfo> findBySourceType(SourceType sourceType);

    List<IndexInfo> findByIndexNameContaining(String keyword);

}

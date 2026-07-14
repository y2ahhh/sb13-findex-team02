package com.findex.team02.indexinfo.repository;

import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.entity.SourceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
//지수 정보 DB 접근 리포지토리
public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>,
    JpaSpecificationExecutor<IndexInfo> {
  // 지수 분류명과 지수명 중복 체크용
  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

    // 분류명 + 지수명으로 조회하는 메서드
    Optional<IndexInfo> findByIndexClassificationAndIndexName(
            String indexClassification,
            String indexName
    );

  List<IndexInfo> findByFavoriteTrue();

  List<IndexInfo> findBySourceType(SourceType sourceType);

  List<IndexInfo> findByIndexNameContaining(String keyword);
}

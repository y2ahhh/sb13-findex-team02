package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository extends JpaRepository<IndexData, Long>,
    IndexDataRepositoryCustom {

    // 특정 지수의 데이터를 최신 기준일자 순으로 조회할 때 사용한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateDesc(Long indexInfoId);

    // 차트 응답처럼 오래된 날짜부터 보여줘야 할 때 사용한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);


    // CSV export에서 최신 날짜순 정렬이 필요할 때 사용한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );


    // 차트 조회에서 기간 내 데이터를 날짜 오름차순으로 가져올 때 사용한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 현재 날짜가 아니라 실제 저장된 최신 기준일자를 조회 기준으로 사용하기 위해 필요하다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(Long indexInfoId);

    // 전체 데이터 기준으로 가장 오래된 기준일자의 데이터를 찾을 때 사용한다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);

    // 기간 성과 계산에서 시작일 이후 가장 가까운 데이터를 찾을 때 사용한다.
    Optional<IndexData> findTopByIndexInfoIdAndBaseDateGreaterThanEqualOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate
    );

    // 기간 성과 계산에서 종료일 이전 가장 가까운 데이터를 찾을 때 사용한다.
    Optional<IndexData> findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate endDate
    );

    // 특정 지수의 특정 기준일 데이터를 조회한다.
    Optional<IndexData> findByIndexInfoAndBaseDate(
            IndexInfo indexInfo,
            LocalDate baseDate
    );
}
package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository
        extends JpaRepository<IndexData, Long>, IndexDataRepositoryCustom {

    // 특정 지수 데이터를 최신 기준일 순으로 조회한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateDesc(
            Long indexInfoId
    );

    // 특정 지수 데이터를 오래된 기준일 순으로 조회한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateAsc(
            Long indexInfoId
    );

    // 특정 지수의 기간 데이터를 최신 기준일 순으로 조회한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 특정 지수의 기간 데이터를 동적 정렬 조건으로 조회한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetween(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );

    // 특정 지수의 전체 데이터를 동적 정렬 조건으로 조회한다.
    List<IndexData> findByIndexInfoId(
            Long indexInfoId,
            Sort sort
    );

    // 모든 지수의 특정 기간 데이터를 동적 정렬 조건으로 조회한다.
    List<IndexData> findByBaseDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );

    // 특정 지수의 기간 데이터를 오래된 기준일 순으로 조회한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 특정 지수의 가장 최신 데이터를 조회한다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(
            Long indexInfoId
    );

    // 특정 지수의 가장 오래된 데이터를 조회한다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateAsc(
            Long indexInfoId
    );

    // 시작일과 같거나 이후인 데이터 중 가장 가까운 데이터를 조회한다.
    Optional<IndexData>
    findTopByIndexInfoIdAndBaseDateGreaterThanEqualOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate
    );

    // 종료일과 같거나 이전인 데이터 중 가장 가까운 데이터를 조회한다.
    Optional<IndexData>
    findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate endDate
    );

    // 특정 지수와 특정 기준일에 해당하는 데이터를 조회한다.
    Optional<IndexData> findByIndexInfoAndBaseDate(
            IndexInfo indexInfo,
            LocalDate baseDate
    );

    // 특정 지수와 특정 기준일의 데이터 존재 여부를 확인한다.
    boolean existsByIndexInfo_IdAndBaseDate(
            Long indexInfoId,
            LocalDate baseDate
    );

    // 특정 지수에 연결된 모든 데이터를 삭제한다.
    void deleteAllByIndexInfoId(
            Long indexInfoId
    );
}
package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import com.findex.team02.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository extends JpaRepository<IndexData, Long>,
    IndexDataRepositoryCustom {

    // 특정 지수의 데이터를 최신 기준일자 순으로 조회할 때 사용한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateDesc(Long indexInfoId);

    // 특정 지수의 데이터를 오래된 기준일자 순으로 조회할 때 사용한다.
    // 예: 차트처럼 시간 흐름에 따라 데이터를 보여줘야 할 때 사용한다.
    List<IndexData> findByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);

    // 특정 지수의 특정 기간 데이터를 최신 기준일자 순으로 조회할 때 사용한다.
    // 예: 기간 내 데이터를 기본적으로 최신순으로 보여줘야 할 때 사용한다.
    // CSV export에서 최신 날짜순 정렬이 필요할 때 사용한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 특정 지수의 특정 기간 데이터를 오래된 기준일자 순으로 조회할 때 사용한다.
    // 예: 차트 조회에서 dataPoints를 날짜 오름차순으로 만들 때 사용한다.
    // CSV export에서 sortField, sortDirection에 따라 동적으로 정렬할 때 사용한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetween(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );

    // 차트 조회에서 기간 내 데이터를 날짜 오름차순으로 가져올 때 사용한다.
    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );


    // 특정 지수의 가장 최신 기준일자 데이터를 조회할 때 사용한다.
    // LocalDate.now()가 아니라 DB에 실제 저장된 최신 baseDate를 기준으로 잡기 위해 필요하다.
    // 예: 차트 조회, 성과 랭킹 조회에서 현재 데이터(currentData)를 찾을 때 사용한다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(Long indexInfoId);

    // 특정 지수의 가장 오래된 기준일자 데이터를 조회할 때 사용한다.
    // 예: 전체 데이터의 시작 지점을 확인하거나, 가장 오래된 기준 데이터를 찾을 때 사용한다.
    Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateAsc(Long indexInfoId);

    // 특정 지수에서 시작일 이후 가장 가까운 기준일자 데이터를 조회할 때 사용한다.
    // 예: 기간 성과 계산에서 startDate와 정확히 일치하는 데이터가 없을 경우,
    // startDate 이후의 가장 가까운 데이터를 beforeData로 사용하기 위해 필요하다.
    Optional<IndexData> findTopByIndexInfoIdAndBaseDateGreaterThanEqualOrderByBaseDateAsc(
            Long indexInfoId,
            LocalDate startDate
    );

    // 특정 지수에서 종료일 이전 가장 가까운 기준일자 데이터를 조회할 때 사용한다.
    // 예: 기간 성과 계산에서 endDate와 정확히 일치하는 데이터가 없을 경우,
    // endDate 이전의 가장 가까운 데이터를 currentData로 사용하기 위해 필요하다.
    Optional<IndexData> findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
            Long indexInfoId,
            LocalDate endDate
    );

    // 특정 지수의 특정 기준일 데이터를 조회한다.
    Optional<IndexData> findByIndexInfoAndBaseDate(
            IndexInfo indexInfo,
            LocalDate baseDate
    );

    boolean existsByIndexInfo_IdAndBaseDate(Long indexInfoId, LocalDate baseDate);
}
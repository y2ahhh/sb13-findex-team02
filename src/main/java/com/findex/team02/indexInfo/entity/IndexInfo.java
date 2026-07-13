package com.findex.team02.indexInfo.entity;

import com.findex.team02.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "index_infos",
        uniqueConstraints = {
                // 같은 분류 안에서 동일한 지수명이 중복 등록되지 않도록 DB 레벨에서 제한한다.
                @UniqueConstraint(
                        name = "uk_index_classification_name",
                        columnNames = {"index_classification", "index_name"}
                )
        }
)
public class IndexInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index_classification", nullable = false)
    private String indexClassification;

    @Column(name = "index_name", nullable = false)
    private String indexName;

    // 지수에 포함된 종목 수
    @Column(name = "employed_items_count", nullable = false)
    private Integer employedItemsCount;

    @Column(name = "base_point_in_time", nullable = false)
    private LocalDate basePointInTime;

    @Column(name = "base_index", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseIndex;

    // 문자열 변경에 따른 데이터 불일치를 막기 위해 SourceType enum으로 관리한다.
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "favorite", nullable = false)
    private Boolean favorite = false;

    public IndexInfo(
            String indexClassification,
            String indexName,
            Integer employedItemsCount,
            LocalDate basePointInTime,
            BigDecimal baseIndex,
            SourceType sourceType,
            Boolean favorite
    ) {
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = favorite != null ? favorite : false;
    }

    // 지수 분류명과 지수명은 중복 방지 기준이므로 수정 대상에서 제외한다.
    public void updateInfo(Integer employedItemsCount, LocalDate basePointInTime,
                           BigDecimal baseIndex, Boolean favorite) {
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
    }

}

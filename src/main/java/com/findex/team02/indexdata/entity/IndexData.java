package com.findex.team02.indexdata.entity;

import com.findex.team02.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "index_data",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_INDEX_DATA_INDEX_INFO_BASE_DATE",
            columnNames = {"index_info_id", "base_date"}
        )
    }
)
public class IndexData extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "index_info_id", nullable = false)
  private IndexInfo indexInfo;

  @Column(name = "base_date", nullable = false)
  private LocalDate baseDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "source_type", nullable = false)
  private SourceType sourceType;

  @Column(name = "market_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal marketPrice;

  @Column(name = "closing_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal closingPrice;

  @Column(name = "high_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal highPrice;

  @Column(name = "low_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal lowPrice;

  @Column(name = "versus", nullable = false, precision = 10, scale = 2)
  private BigDecimal versus;

  @Column(name = "fluctuation_rate", nullable = false, precision = 10, scale = 2)
  private BigDecimal fluctuationRate;

  @Column(name = "trading_quantity", nullable = false)
  private Long tradingQuantity;

  @Column(name = "trading_price", nullable = false)
  private Long tradingPrice;

  @Column(name = "market_total_amount", nullable = false)
  private Long marketTotalAmount;


  public IndexData(IndexInfo indexInfo, LocalDate baseDate, SourceType sourceType,
      BigDecimal marketPrice, BigDecimal closingPrice, BigDecimal highPrice, BigDecimal lowPrice,
      BigDecimal versus, BigDecimal fluctuationRate, Long tradingQuantity, Long tradingPrice,
      Long marketTotalAmount) {
    this.indexInfo = indexInfo;
    this.baseDate = baseDate;
    this.sourceType = sourceType;
    this.marketPrice = marketPrice;
    this.closingPrice = closingPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.tradingQuantity = tradingQuantity;
    this.tradingPrice = tradingPrice;
    this.marketTotalAmount = marketTotalAmount;
  }

  public void update(
      BigDecimal marketPrice,
      BigDecimal closingPrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      Long tradingQuantity,
      Long tradingPrice,
      Long marketTotalAmount
  ) {
    if (marketPrice != null) {
      this.marketPrice = marketPrice;
    }

    if (closingPrice != null) {
      this.closingPrice = closingPrice;
    }

    if (highPrice != null) {
      this.highPrice = highPrice;
    }

    if (lowPrice != null) {
      this.lowPrice = lowPrice;
    }

    if (versus != null) {
      this.versus = versus;
    }

    if (fluctuationRate != null) {
      this.fluctuationRate = fluctuationRate;
    }

    if (tradingQuantity != null) {
      this.tradingQuantity = tradingQuantity;
    }

    if (tradingPrice != null) {
      this.tradingPrice = tradingPrice;
    }

    if (marketTotalAmount != null) {
      this.marketTotalAmount = marketTotalAmount;
    }
  }
}

package com.findex.team02.indexInfo.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.findex.team02.autosync.repository.AutoSyncConfigRepository;
import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.dto.request.IndexInfoCreateRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.mapper.IndexInfoMapper;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import com.findex.team02.indexinfo.service.BasicIndexInfoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class BasicIndexInfoServiceTest {
  @InjectMocks
  private BasicIndexInfoService basicIndexInfoService;
  @Mock
  private IndexInfoRepository indexInfoRepository;
  @Mock
  private IndexInfoMapper indexInfoMapper;
  @Mock
  private AutoSyncConfigRepository autoSyncConfigRepository;
  @Mock
  private IndexDataRepository indexDataRepository;
  @Test
  @DisplayName("지수 정보 등록 및 자동 연동 설정 비활성화 상태 초기화 성공")
  void createIndexInfo_success() {
    // given
    IndexInfoCreateRequest request = new IndexInfoCreateRequest(
        "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2000, 1, 1), new BigDecimal("1000.0"), false
    );
    IndexInfo savedInfo = IndexInfo.builder()
        .indexClassification(request.indexClassification())
        .indexName(request.indexName())
        .employedItemsCount(request.employedItemsCount())
        .basePointInTime(request.basePointInTime())
        .baseIndex(request.baseIndex())
        .favorite(false)
        .sourceType(SourceType.USER)
        .build();

    IndexInfoDto dto = new IndexInfoDto(
        1L, "KOSPI시리즈", "IT 서비스", 200, LocalDate.of(2000, 1, 1), new BigDecimal("1000.0"), SourceType.USER, false
    );
    when(indexInfoRepository.existsByIndexClassificationAndIndexName(any(), any())).thenReturn(false);
    when(indexInfoRepository.save(any(IndexInfo.class))).thenReturn(savedInfo);
    when(indexInfoMapper.toDto(any(IndexInfo.class))).thenReturn(dto);
    // when
    IndexInfoDto result = basicIndexInfoService.createIndexInfo(request);
    // then
    assertThat(result.indexName()).isEqualTo("IT 서비스");
    verify(indexInfoRepository).save(any(IndexInfo.class));
    verify(autoSyncConfigRepository).save(any(AutoSyncConfig.class)); // 연동 설정 동시 생성 여부 검증
  }
  @Test
  @DisplayName("지수 정보 삭제 시 연관된 IndexData 및 AutoSyncConfig가 함께(Cascade) 삭제되어야 한다")
  void deleteIndexInfo_success() {
    // given
    Long targetId = 1L;
    when(indexInfoRepository.existsById(targetId)).thenReturn(true);
    // when
    basicIndexInfoService.deleteIndexInfo(targetId);
    // then
    verify(indexDataRepository).deleteAllByIndexInfoId(targetId);
    verify(autoSyncConfigRepository).deleteByIndexInfoId(targetId);
    verify(indexInfoRepository).deleteById(targetId);
  }
  @Test
  @DisplayName("PATCH 시 일부 값만 넘겨도(null 포함) null로 덮어쓰지 않고 잘 유지하며 수정되어야 한다")
  void updateIndexInfo_success() {
    // given
    Long targetId = 1L;
    IndexInfo mockInfo = IndexInfo.builder()
        .employedItemsCount(100)
        .baseIndex(new BigDecimal("100.0"))
        .build();

    // favorite 필드만 true로 수정 시도
    IndexInfoUpdateRequest request = new IndexInfoUpdateRequest(
        null, null, null, true
    );
    when(indexInfoRepository.findById(targetId)).thenReturn(Optional.of(mockInfo));
    // when
    basicIndexInfoService.updateIndexInfo(targetId, request);
    // then
    // null이었던 부분은 원래 값을 유지해야 함
    assertThat(mockInfo.getEmployedItemsCount()).isEqualTo(100);
    assertThat(mockInfo.getBaseIndex()).isEqualTo(new BigDecimal("100.0"));
    // favorite은 업데이트 되어야 함
    assertThat(mockInfo.isFavorite()).isTrue();
  }
}
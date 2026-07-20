package com.findex.team02.indexinfo.service;

import com.findex.team02.autosync.entity.AutoSyncConfig;
import com.findex.team02.autosync.repository.AutoSyncConfigRepository;
import com.findex.team02.global.type.SourceType;
import com.findex.team02.indexdata.repository.IndexDataRepository;
import com.findex.team02.indexinfo.dto.request.IndexInfoCreateRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoSummaryDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.mapper.IndexInfoMapper;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import com.findex.team02.indexinfo.repository.projection.IndexInfoSummary;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("indexName", "employedItemsCount", "indexClassification");

    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final IndexDataRepository indexDataRepository;

    // ─── 지수 정보 등록 ─────────────────────────────────────────
    @Override
    @Transactional
    public IndexInfoDto createIndexInfo(IndexInfoCreateRequest request) {

        // ① 중복 체크: 같은 분류명 + 지수명 조합이 이미 있으면 예외를 던집니다.
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
            request.indexClassification(),
            request.indexName())) {
            throw new IllegalArgumentException("이미 존재하는 지수 정보입니다.");
        }

        // ② Entity 생성: 요청 DTO의 값을 Builder 패턴으로 채웁니다.
        //    사용자가 직접 등록하므로 sourceType은 USER로 고정합니다.
        IndexInfo indexInfo = IndexInfo.builder()
            .indexClassification(request.indexClassification())
            .indexName(request.indexName())
            .employedItemsCount(request.employedItemsCount())
            .basePointInTime(request.basePointInTime())
            .baseIndex(request.baseIndex())
            .sourceType(SourceType.USER)
            .favorite(request.favorite())
            .build();

        // ③ DB 저장
        IndexInfo saved = indexInfoRepository.save(indexInfo);
        // 자동 연동 설정 비활성화 상태로 동시 생성
        AutoSyncConfig autoSyncConfig = new AutoSyncConfig(saved, false);
        autoSyncConfigRepository.save(autoSyncConfig);

        // ④ 저장된 Entity -> DTO로 변환 후 반환 (Entity를 직접 반환하지 않습니다)
        return indexInfoMapper.toDto(saved);
    }

    // ─── 지수 정보 수정 ─────────────────────────────────────────
    @Override
    @Transactional
    public IndexInfoDto updateIndexInfo(Long id, IndexInfoUpdateRequest request) {

        // ① DB에서 해당 ID의 지수 정보를 조회합니다. 없으면 예외를 던집니다.
        IndexInfo indexInfo = indexInfoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 지수 정보를 찾을 수 없습니다. id=" + id));

        // ② Entity의 비즈니스 메서드를 호출하여 값을 변경합니다.
        //    (Setter를 직접 쓰지 않고 의미 있는 메서드를 통해 수정합니다.)
        indexInfo.updateInfo(
            request.employedItemsCount(),
            request.basePointInTime(),
            request.baseIndex(),
            request.favorite()
        );

        // ③ @Transactional 덕분에 별도의 save() 없이 변경사항이 자동 반영됩니다. (더티 체킹)
        return indexInfoMapper.toDto(indexInfo);
    }

    // ─── 지수 정보 삭제 ─────────────────────────────────────────
    @Override
    @Transactional
    public void deleteIndexInfo(Long id) {

        // ① 존재 여부 먼저 확인합니다. 없으면 예외를 던집니다.
        if (!indexInfoRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 지수 정보를 찾을 수 없습니다. id=" + id);
        }

        // ② IndexData 삭제
        indexDataRepository.deleteAllByIndexInfoId(id);

        // ③ AutoSyncConfig 삭제
        autoSyncConfigRepository.deleteByIndexInfoId(id);

        // ④ IndexInfo 삭제
        indexInfoRepository.deleteById(id);
    }

    // ─── 지수 정보 단건 조회 ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public IndexInfoDto getIndexInfo(Long id) {

        // ① DB에서 조회합니다. 없으면 예외를 던집니다.
        IndexInfo indexInfo = indexInfoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 지수 정보를 찾을 수 없습니다. id=" + id));

        // ② Entity -> DTO 변환 후 반환합니다.
        return indexInfoMapper.toDto(indexInfo);
    }

    @Override
    public CursorPageResponseIndexInfoDto getIndexInfos(IndexInfoSearchRequest request) {
        validateRequest(request);

        int size = request.size();

        List<IndexInfo> indexInfos = indexInfoRepository.findAllByCondition(request);

        List<IndexInfoDto> content = indexInfoMapper.toDto(indexInfos);

        long totalElements = indexInfoRepository.countTotalElements(request);

        // 조회 결과가 없는 경우 빈 리스트 처리
        if (indexInfos.isEmpty()) {
            return new CursorPageResponseIndexInfoDto(
                    List.of(),
                    null,
                    null,
                    size,
                    0L,
                    false
            );
        }

        boolean hasNext = indexInfos.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        IndexInfo last = indexInfos.get(indexInfos.size() - 1);

        // [수정] nextCursor를 항상 id로 내려주던 버그 수정
        // sortField에 따라 비교 컬럼이 다른데(indexName/employedItemsCount/indexClassification)
        // cursor를 id로만 주면 다음 페이지 조회가 엉뚱하게 비교되어 페이지네이션이 깨짐
        // (프론트 RangeError 원인) -> sortField에 맞는 필드값을 cursor로 반환하도록 수정
        String nextCursor = switch (request.sortField() == null ? "" : request.sortField()) {
            case "indexName" -> last.getIndexName();
            case "employedItemsCount" -> String.valueOf(last.getEmployedItemsCount());
            default -> last.getIndexClassification();
        };

        return new CursorPageResponseIndexInfoDto(
                content,
                nextCursor,
                last.getId(),
                size,
                totalElements,
                hasNext
        );
    }

    @Override
    public List<IndexInfoSummaryDto> getIndexInfoSummary() {
        List<IndexInfoSummary> indexInfos = indexInfoRepository.findAllSummaryBy();

        return indexInfos.stream()
                .map(s -> new IndexInfoSummaryDto(
                        s.getId(),
                        s.getIndexClassification(),
                        s.getIndexName()
                ))
                .toList();
    }

    // 요청값 검증
    private void validateRequest(IndexInfoSearchRequest request) {
        if (request.size() <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다. 요청값: " + request.size());
        }

        if (StringUtils.hasText(request.sortField())
                && !ALLOWED_SORT_FIELDS.contains(request.sortField())) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + request.sortField());
        }

        // cursor와 idAfter는 항상 쌍으로 와야 함 (하나만 있으면 페이지네이션 조건이 깨짐)
        boolean hasCursor = StringUtils.hasText(request.cursor());
        boolean hasIdAfter = request.idAfter() != null;

        if (hasCursor != hasIdAfter) {
            throw new IllegalArgumentException("cursor와 idAfter는 함께 전달되어야 합니다.");
        }
    }

}

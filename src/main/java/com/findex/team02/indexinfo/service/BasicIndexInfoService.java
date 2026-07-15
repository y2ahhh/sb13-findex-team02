package com.findex.team02.indexinfo.service;

import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.mapper.IndexInfoMapper;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("indexName", "employedItemsCount", "indexClassification");

    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;

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

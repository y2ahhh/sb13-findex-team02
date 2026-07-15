package com.findex.team02.indexinfo.service;

import com.findex.team02.indexinfo.dto.request.IndexInfoSearchRequest;
import com.findex.team02.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.findex.team02.indexinfo.dto.response.IndexInfoDto;
import com.findex.team02.indexinfo.entity.IndexInfo;
import com.findex.team02.indexinfo.mapper.IndexInfoMapper;
import com.findex.team02.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;

    @Override
    public CursorPageResponseIndexInfoDto getIndexInfos(IndexInfoSearchRequest request) {
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

}

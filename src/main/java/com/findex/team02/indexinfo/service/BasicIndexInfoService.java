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
            indexInfos.remove(size);
        }

        List<IndexInfoDto> content = indexInfoMapper.toDto(indexInfos);

        IndexInfo last = indexInfos.get(indexInfos.size() - 1);


        return new CursorPageResponseIndexInfoDto(
                content,
                last.getId().toString(),
                last.getId(),
                size,
                totalElements,
                hasNext
        );
    }

}

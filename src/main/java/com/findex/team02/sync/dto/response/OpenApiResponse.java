package com.findex.team02.sync.dto.response;

import java.util.List;

public record OpenApiResponse(
        Response response
) {

    public record Response(
            Header header,
            Body body
    ) {
    }

    public record Header(
            String resultCode,
            String resultMsg
    ) {
    }

    public record Body(
            Items items,
            Integer numOfRows,
            Integer pageNo,
            Integer totalCount
    ) {
    }

    public record Items(
            List<OpenApiItemDto> item
    ) {
    }
}

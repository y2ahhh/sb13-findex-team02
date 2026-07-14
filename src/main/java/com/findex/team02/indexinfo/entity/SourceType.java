package com.findex.team02.indexinfo.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "데이터 출처")
public enum SourceType {

    @Schema(description = "사용자가 직접 등록한 데이터")
    USER,

    @Schema(description = "외부 Open API를 통해 수집한 데이터")
    OPEN_API

}

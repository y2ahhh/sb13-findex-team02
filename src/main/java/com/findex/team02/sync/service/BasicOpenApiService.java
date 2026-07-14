package com.findex.team02.sync.service;

import com.findex.team02.sync.dto.response.OpenApiItemDto;
import com.findex.team02.sync.dto.response.OpenApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicOpenApiService implements OpenApiService {

    private final RestClient restClient;

    @Value("${openapi.api-key}")
    private String apiKey;

    @Override
    public List<OpenApiItemDto> getIndexData(LocalDate date) {

        String basDt = date.format(DateTimeFormatter.BASIC_ISO_DATE);

        OpenApiResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/1160100/service/GetMarketIndexInfoService/getStockMarketIndex")
                        .queryParam("serviceKey", apiKey)
                        .queryParam("resultType", "json")
                        .queryParam("basDt", basDt)
                        .build())
                .retrieve()
                .body(OpenApiResponse.class);

        if (response == null
                || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null) {
            return List.of();
        }

        return response.response().body().items().item();
    }
}
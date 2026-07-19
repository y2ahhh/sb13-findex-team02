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

    private static final int LATEST_DATA_FETCH_SIZE = 1000;

    private final RestClient restClient;

    @Value("${openapi.api-key}")
    private String apiKey;

    @Override
    public List<OpenApiItemDto> getIndexData(LocalDate date) {

        String basDt = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        OpenApiResponse response = callApi(basDt, null);

        return extractItems(response);
    }

    @Override
    public LocalDate findLatestAvailableDate() {

        OpenApiResponse response = callApi(null, 1);
        List<OpenApiItemDto> items = extractItems(response);

        if (items.isEmpty()) {
            throw new IllegalStateException("Open API에서 데이터를 하나도 찾을 수 없습니다.");
        }

        return LocalDate.parse(items.get(0).basDt(), DateTimeFormatter.BASIC_ISO_DATE);
    }

    @Override
    public List<OpenApiItemDto> getLatestIndexData() {

        OpenApiResponse response = callApi(null, LATEST_DATA_FETCH_SIZE);
        List<OpenApiItemDto> items = extractItems(response);

        if (items.isEmpty()) {
            throw new IllegalStateException("Open API에서 데이터를 하나도 찾을 수 없습니다.");
        }

        String latestBasDt = items.get(0).basDt();

        return items.stream()
                .filter(item -> latestBasDt.equals(item.basDt()))
                .toList();
    }

    private OpenApiResponse callApi(String basDt, Integer numOfRows) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .scheme("https")
                            .host("apis.data.go.kr")
                            .path("/1160100/service/GetMarketIndexInfoService/getStockMarketIndex")
                            .queryParam("serviceKey", apiKey)
                            .queryParam("resultType", "json");

                    if (basDt != null) {
                        uriBuilder.queryParam("basDt", basDt);
                    }
                    if (numOfRows != null) {
                        uriBuilder.queryParam("numOfRows", numOfRows);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(OpenApiResponse.class);
    }

    private List<OpenApiItemDto> extractItems(OpenApiResponse response) {
        if (response == null
                || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null) {
            return List.of();
        }

        return response.response().body().items().item();
    }
}
// src/main/java/code/project/external/HiraEmergencyApiClient.java
package code.project.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class HiraEmergencyApiClient {

    // WebClientConfig에서 @Bean(name = "hiraWebClient") 로 등록했다면 명시적으로 바인딩
    @Qualifier("hiraWebClient")
    private final WebClient hiraWebClient;

    @Value("${app.hira.service-key}")
    private String serviceKey;

    /**
     * 병원 목록(JSON Map)
     */
    public Mono<Map> getHospitalList(String q0, String q1, int page, int size) {
        Function<UriBuilder, java.net.URI> uriFn = uriBuilder -> uriBuilder
                .path("/getHsptlMdcncListInfoInqire")
                .queryParam("serviceKey", serviceKey)
                .queryParam("_type", "json")
                .queryParam("pageNo", page)
                .queryParam("numOfRows", size)
                .queryParam("Q0", q0)
                .queryParam("Q1", q1)
                .build();

        return hiraWebClient.get()
                .uri(uriFn)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(e -> log.error("HIRA Hospital API error: {}", e.getMessage(), e));
    }

    /**
     * 병원 목록 RAW(String) — 디버그용
     */
    public Mono<String> getHospitalListRaw(String q0, String q1, int page, int size) {
        return hiraWebClient.get()
                .uri(b -> b.path("/getHsptlMdcncListInfoInqire")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("_type", "json")
                        .queryParam("pageNo", page)
                        .queryParam("numOfRows", size)
                        .queryParam("Q0", q0)
                        .queryParam("Q1", q1)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 약국 목록(JSON Map)
     */
    public Mono<Map> getPharmacyList(String q0, String q1, int page, int size) {
        return hiraWebClient.get()
                .uri(b -> b.path("/getParmacyListInfoInqire") // 오타 아님(원 API 표기)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("_type", "json")
                        .queryParam("pageNo", page)
                        .queryParam("numOfRows", size)
                        .queryParam("Q0", q0)
                        .queryParam("Q1", q1)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(e -> log.error("HIRA Pharmacy API error: {}", e.getMessage(), e));
    }

    /**
     * 약국 목록 RAW(String) — 디버그용
     */
    public Mono<String> getPharmacyListRaw(String q0, String q1, int page, int size) {
        return hiraWebClient.get()
                .uri(b -> b.path("/getParmacyListInfoInqire")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("_type", "json")
                        .queryParam("pageNo", page)
                        .queryParam("numOfRows", size)
                        .queryParam("Q0", q0)
                        .queryParam("Q1", q1)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}

// src/main/java/code/project/external/PharmacyApiClient.java
package code.project.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PharmacyApiClient {

    @Qualifier("pharmWebClient")
    private final WebClient pharmWebClient;

    @Value("${app.pharm.service-key}")
    private String serviceKey;

    @Value("${app.pharm.list-path:/getPharmacyData.do}")
    private String listPath;

    /** 약국 목록(JSON Map) - SafeMap 규격 */
    public Mono<Map> getPharmacyList(String q0, String q1, int page, int size) {
        return pharmWebClient.get()
                .uri(b -> {
                    var ub = b.path(listPath)
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("type", "json")      // SafeMap은 type=json
                            .queryParam("pageNo", page)
                            .queryParam("numOfRows", size);

                    // 지역 필드는 문서마다 다소 차이 — 가장 흔한 이름으로 시도/시군구 맵핑
                    if (q0 != null && !q0.isBlank()) ub.queryParam("sidoNm", q0);  // 예: 경기도
                    if (q1 != null && !q1.isBlank()) ub.queryParam("sggNm", q1);   // 예: 성남시분당구

                    return ub.build();
                })
                .exchangeToMono(res -> {
                    if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(Map.class);
                    }
                    return res.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> {
                                log.error("PHARM API non-2xx: status={} body={}", res.statusCode(), body);
                                return Mono.error(new IllegalStateException("PHARM API error: " + res.statusCode().value()));
                            });
                })
                .doOnError(e -> log.error("PHARM API call failed: {}", e.toString(), e));
    }

    /** RAW 문자열 (디버그용) */
    public Mono<String> getPharmacyListRaw(String q0, String q1, int page, int size) {
        return pharmWebClient.get()
                .uri(b -> {
                    var ub = b.path(listPath)
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("type", "json")
                            .queryParam("pageNo", page)
                            .queryParam("numOfRows", size);
                    if (q0 != null && !q0.isBlank()) ub.queryParam("sidoNm", q0);
                    if (q1 != null && !q1.isBlank()) ub.queryParam("sggNm", q1);
                    return ub.build();
                })
                .exchangeToMono(res ->
                        res.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> {
                                    log.info("PHARM RAW status={} body={}", res.statusCode(), body);
                                    return body;
                                })
                );
    }
}

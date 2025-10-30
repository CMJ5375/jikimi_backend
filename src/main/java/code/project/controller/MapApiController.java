package code.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/project/map")
public class MapApiController {

    // 카카오 REST API KEY
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @GetMapping("/reverse")
    public ResponseEntity<String> reverseGeocodeMap(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon) {
        return callKakaoCoord2Address(lat, lon);
    }

    private ResponseEntity<String> callKakaoCoord2Address(double lat, double lon) {
        String url = String.format(
                "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%f&y=%f",
                lon, lat
        );
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            headers.set("Accept", "application/json;charset=UTF-8");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();

            log.info("Kakao coord2address 요청: {}", url);
            ResponseEntity<String> kakaoResponse =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            HttpStatusCode rawStatus = kakaoResponse.getStatusCode();
            HttpStatus status = HttpStatus.resolve(rawStatus.value());
            if (status == null) status = HttpStatus.OK; // null 방지 fallback

            log.info("Kakao 응답 상태: {}", status);

            return ResponseEntity.status(status).body(kakaoResponse.getBody());

        } catch (Exception e) {
            log.error("Kakao coord2address 호출 중 예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"KAKAO_COORD2ADDRESS_FAILED\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

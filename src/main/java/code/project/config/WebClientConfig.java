package code.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

// src/main/java/code/project/config/WebClientConfig.java
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Value("${app.hira.base-url}")
    private String hiraBaseUrl;
    @Value("${app.hira.timeout-sec:5}")
    private int hiraTimeout;

    @Value("${app.pharm.base-url}")
    private String pharmBaseUrl;
    @Value("${app.pharm.timeout-sec:5}")
    private int pharmTimeout;

    @Bean(name = "hiraWebClient")
    public WebClient hiraWebClient() {
        HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(hiraTimeout));
        return WebClient.builder()
                .baseUrl(hiraBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean(name = "pharmWebClient")
    public WebClient pharmWebClient() {
        HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(pharmTimeout));
        return WebClient.builder()
                .baseUrl(pharmBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();
    }
}


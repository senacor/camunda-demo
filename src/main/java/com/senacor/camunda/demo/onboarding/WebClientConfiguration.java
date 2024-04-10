package com.senacor.camunda.demo.onboarding;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    WebClient buildWebClient(WebClient.Builder webclientBuilder) {
        return webclientBuilder.baseUrl("http://localhost:8080").build();
    }
}

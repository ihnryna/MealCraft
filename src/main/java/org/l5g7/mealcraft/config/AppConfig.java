package org.l5g7.mealcraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "externalApiClient")
    public RestClient externalRecipeApiClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://www.themealdb.com/api/json/v1/1")
                .build();
    }

    @Bean(name = "internalApiClient")
    public RestClient internalApiClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}

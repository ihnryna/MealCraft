package org.l5g7.mealcraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Configuration
@EnableScheduling
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
    @Qualifier("internalApiClient")
    public RestClient internalApiClient(RestClient.Builder builder,
                                        @Value("${jwt.cookie-name}") String jwtCookieName) {
        return builder
                .baseUrl("http://localhost:8080")
                .requestInterceptor((req, body, exec) -> {
                    var attrs = RequestContextHolder.getRequestAttributes();
                    if (attrs instanceof ServletRequestAttributes sra) {
                        Cookie[] cookies = sra.getRequest().getCookies();
                        if (cookies != null) {
                            Arrays.stream(cookies)
                                    .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                                    .findFirst()
                                    .ifPresent(cookie -> req.getHeaders()
                                            .add(HttpHeaders.COOKIE, cookie.getName() + "=" + cookie.getValue()));
                        }
                    }
                    return exec.execute(req, body);
                })
                .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}

package org.l5g7.mealcraft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN_STR = "ADMIN";
    private static final String USER_STR = "USER";
    private static final String PREMIUM_USER_STR = "PREMIUM_USER";


    private final OncePerRequestFilter jwtCookieFilter;

    public SecurityConfig(OncePerRequestFilter jwtCookieFilter) {
        this.jwtCookieFilter = jwtCookieFilter;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)               // <-- вимкнути CSRF
                .headers(h -> h.frameOptions(f -> f.sameOrigin())) // для H2 console
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers(
                                "/css/**",
                                "/images/**",
                                "/webjars/**",
                                "/templates/**"
                        ).permitAll()
                        .requestMatchers("/mealcraft/login", "/mealcraft/register", "/mealcraft/landing").permitAll()
                        .requestMatchers("/mealcraft/admin/**").hasRole(ADMIN_STR)
                        .requestMatchers("/mealcraft/**").hasAnyRole(ADMIN_STR, USER_STR, PREMIUM_USER_STR)
                        .anyRequest().hasAnyRole(ADMIN_STR, USER_STR, PREMIUM_USER_STR)

                )
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults());
        return http.build();
    }
}

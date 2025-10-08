package org.l5g7.mealcraft.config;

import org.l5g7.mealcraft.app.auth.security.JwtCookieFilter;
import org.l5g7.mealcraft.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final OncePerRequestFilter jwtCookieFilter;

    public SecurityConfig(OncePerRequestFilter jwtCookieFilter) {
        this.jwtCookieFilter = jwtCookieFilter;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())               // <-- вимкнути CSRF
                .headers(h -> h.frameOptions(f -> f.sameOrigin())) // для H2 console
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/**").permitAll()   // або .authenticated() якщо вже є логін
                        .anyRequest().permitAll()
                )
                .cors(Customizer.withDefaults());
        return http.build();
    }
}

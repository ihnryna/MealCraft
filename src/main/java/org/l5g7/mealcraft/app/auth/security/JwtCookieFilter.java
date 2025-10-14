package org.l5g7.mealcraft.app.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {
    @Value("${jwt.cookie-name}")
    private String authToken;

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public JwtCookieFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (authToken.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtService.validateToken(token)) {
                        String username = jwtService.getUsernameFromToken(token);

                        if (username == null || userRepository.findByUsername(username).isEmpty()) {
                            filterChain.doFilter(request, response);
                            return;
                        }

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        new User(username, "", Collections.emptyList()),
                                        null,
                                        Collections.emptyList()
                                );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

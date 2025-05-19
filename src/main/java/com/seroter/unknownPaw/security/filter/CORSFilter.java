package com.seroter.unknownPaw.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public class CORSFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000"  // 개발 환경

    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Origin",
            "X-Requested-With",
            "Content-Type",
            "Accept",
            "Key",
            "Authorization"
    );

    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Authorization"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", String.join(",", ALLOWED_METHODS));
            response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOWED_HEADERS));
            response.setHeader("Access-Control-Expose-Headers", String.join(",", EXPOSED_HEADERS));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
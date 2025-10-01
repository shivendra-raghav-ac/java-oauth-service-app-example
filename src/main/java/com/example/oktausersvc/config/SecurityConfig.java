package com.example.oktausersvc.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Optional: API key guard for POC (Header: X-API-Key). 
@Configuration
@ConditionalOnProperty(prefix = "security.apiKey", name = "enabled", havingValue = "true")
@Order(1)
public class SecurityConfig extends OncePerRequestFilter {

    private final String apiKeyValue;

    public SecurityConfig(AppProperties props) {
        this.apiKeyValue = props.getSecurity().getApiKey().getValue();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String provided = req.getHeader("X-API-Key");
        if (provided == null || !provided.equals(apiKeyValue)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}

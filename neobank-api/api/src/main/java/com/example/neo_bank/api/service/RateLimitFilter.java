package com.example.neo_bank.api.service;

import com.example.neo_bank.api.ratelimit.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad de red para prevenir ataques de denegación de servicio (DDoS)
 * y abuso de la API.
 * <p>
 * Implementa el algoritmo <b>Token Bucket</b> utilizando la librería Bucket4j.
 * Restringe el número de peticiones permitidas por dirección IP en un intervalo de tiempo.
 * Si se supera el límite, rechaza la petición con estado HTTP 429 (Too Many Requests).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitingService rateLimitingService;

    public RateLimitFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();

        Bucket bucket = rateLimitingService.resolveBucket(ip);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        }
        else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Has hecho demasiadas peticiones (El máximo es 100 por minuto)");
        }
    }
}

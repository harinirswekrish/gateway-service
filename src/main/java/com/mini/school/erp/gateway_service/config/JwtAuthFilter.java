package com.mini.school.erp.gateway_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.school.erp.gateway_service.util.AppContant;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, GatewayFilter {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        HttpMethod method = exchange.getRequest().getMethod();

        // Skip auth endpoints
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.error(exchange, HttpStatus.UNAUTHORIZED, AppContant.AUTHORIZATION_VALID);
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            return tokenService.validateTokenPresent(username, token)
                    .flatMap(valid -> {
                        if (!valid) {
                            return this.error(exchange, HttpStatus.UNAUTHORIZED, AppContant.INVALID_TOKEN);
                        }
                        if (jwtUtil.isTokenExpired(token)) {
                            return this.error(exchange, HttpStatus.UNAUTHORIZED, AppContant.TOKEN_EXPIRED);
                        }

                        // ==========================
                        // RBAC Rules
                        // ==========================
                        if (path.startsWith("/students")) {
                            if (method == HttpMethod.POST && !AppContant.ADMIN.equals(role)) {
                                return deny(exchange, AppContant.ADMIN_VALIDATE);
                            }
                            if (method == HttpMethod.GET &&
                                    !(role.equals(AppContant.ADMIN) || role.equals(AppContant.STAFF))) {
                                return deny(exchange, AppContant.ACCESS_DENIED_STUDENT);
                            }
                        }

                        if (path.startsWith("/courses")) {
                            if (method == HttpMethod.POST && !AppContant.ADMIN.equals(role)) {
                                return deny(exchange, AppContant.COURSE_VALIDATE);
                            }
                            if (method == HttpMethod.GET &&
                                    !(role.equals(AppContant.ADMIN) || role.equals(AppContant.STAFF))) {
                                return deny(exchange, AppContant.ACCESS_DENIED_COURSE);
                            }
                        }

                        if (path.startsWith("/enrollments")) {
                            if (method == HttpMethod.POST && !AppContant.ADMIN.equals(role)) {
                                return deny(exchange, AppContant.ADMIN_ENROLL_STUDENT);
                            }
                            if (method == HttpMethod.GET &&
                                    !(role.equals(AppContant.ADMIN) || role.equals(AppContant.STAFF))) {
                                return deny(exchange, AppContant.ACCESS_DENIED_ENROLL);
                            }
                            if (method == HttpMethod.DELETE && !AppContant.ADMIN.equals(role)) {
                                return deny(exchange, AppContant.ADMIN_REVERT);
                            }
                        }

                        if (path.startsWith("/attendances")) {
                            if (method == HttpMethod.POST && !AppContant.STAFF.equals(role)) {
                                return deny(exchange, AppContant.STAFF_MARK_ATTENDANCE);
                            }
                            if (method == HttpMethod.GET &&
                                    !(role.equals(AppContant.ADMIN) || role.equals(AppContant.STAFF))) {
                                return deny(exchange, AppContant.ACCESS_DENIED_VIEW_ATTENDANCE);
                            }
                        }

                        return chain.filter(exchange);
                    });

        } catch (Exception e) {
            return this.error(exchange, HttpStatus.UNAUTHORIZED, AppContant.INVALID_TOKEN);
        }
    }

    private Mono<Void> error(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body;
        try {
            body = new ObjectMapper().writeValueAsString(Collections.singletonMap("error", message));
        } catch (Exception e) {
            body = "{\"error\":\"" + message + "\"}";
        }

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private Mono<Void> deny(ServerWebExchange exchange, String message) {
        return this.error(exchange, HttpStatus.FORBIDDEN, message);
    }
}

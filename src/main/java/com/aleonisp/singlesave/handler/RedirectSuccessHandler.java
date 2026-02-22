package com.aleonisp.singlesave.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.net.URI;

public class RedirectSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final URI location;

    public RedirectSuccessHandler(String location) {
        this.location = URI.create(location);
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                              Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(location);
        return response.setComplete();
    }
}
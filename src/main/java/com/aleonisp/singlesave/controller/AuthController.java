package com.aleonisp.singlesave.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class AuthController {

    @GetMapping("/login/google")
    public Mono<Void> loginGoogle(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse()
                .getHeaders()
                .setLocation(URI.create("/oauth2/authorization/google?prompt=select_account"));
        return exchange.getResponse().setComplete();
    }

    @GetMapping("/api/logout")
    public Mono<Void> logout(ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(WebSession::invalidate)
                .then();
    }
}
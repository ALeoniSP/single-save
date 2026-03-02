package com.aleonisp.singlesave.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class AuthController {

    private static final String REDIRECT_AFTER_LOGIN = "redirect_after_login";

    @GetMapping("/login/google")
    public Mono<Void> loginGoogle(ServerWebExchange exchange) {
        String redirect = exchange.getRequest().getQueryParams().getFirst("redirect");
        String target = (redirect == null || redirect.isBlank()) ? "/" : redirect;

        return exchange.getSession()
                .doOnNext(session -> session.getAttributes().put(REDIRECT_AFTER_LOGIN, target))
                .then(Mono.fromRunnable(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders()
                            .setLocation(URI.create("/oauth2/authorization/google?prompt=select_account"));
                }))
                .then(exchange.getResponse().setComplete());
    }
}
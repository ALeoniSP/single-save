package com.aleonisp.singlesave.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/me")
    public Mono<Map<String, Object>> me(@AuthenticationPrincipal OidcUser user) {
        return Mono.just(Map.of(
                "sub", user.getSubject(),
                "email", user.getEmail(),
                "name", user.getFullName()
        ));
    }
}
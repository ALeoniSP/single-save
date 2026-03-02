package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.service.SavePointService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/save-points")
public class SavePointController {

    private final SavePointService service;

    public SavePointController(SavePointService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SavePointResponse> create(OAuth2AuthenticationToken auth,
                                          @AuthenticationPrincipal OidcUser user,
                                          @RequestBody Mono<CreateSavePointRequest> reqMono) {
        String provider = auth.getAuthorizedClientRegistrationId();
        String subject = user.getSubject();

        return reqMono.map(CreateSavePointRequest::action)
                .flatMap(action -> service.create(provider, subject, action));
    }

    @GetMapping
    public Flux<SavePointResponse> list(OAuth2AuthenticationToken auth,
                                        @AuthenticationPrincipal OidcUser user,
                                        @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return service.list(auth.getAuthorizedClientRegistrationId(), user.getSubject(), limit);
    }

    @GetMapping("/latest")
    public Mono<SavePointResponse> latest(OAuth2AuthenticationToken auth,
                                          @AuthenticationPrincipal OidcUser user) {
        return service.latest(auth.getAuthorizedClientRegistrationId(), user.getSubject());
    }
}
package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.service.SavePointService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/save-points")
public class SavePointController {

    private static final String PROVIDER = "google";

    private final SavePointService service;

    public SavePointController(SavePointService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SavePointResponse> create(@AuthenticationPrincipal OidcUser user,
                                          @RequestBody Mono<CreateSavePointRequest> reqMono) {
        String sub = user.getSubject();
        return reqMono.map(CreateSavePointRequest::action)
                .flatMap(action -> service.create(PROVIDER, sub, action));
    }

    @GetMapping
    public Flux<SavePointResponse> list(@AuthenticationPrincipal OidcUser user,
                                        @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return service.list(PROVIDER, user.getSubject(), limit);
    }

    @GetMapping("/latest")
    public Mono<SavePointResponse> latest(@AuthenticationPrincipal OidcUser user) {
        return service.latest(PROVIDER, user.getSubject());
    }
}
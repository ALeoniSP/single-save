package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.dto.SuggestionResponse;
import com.aleonisp.singlesave.service.SavePointService;
import com.aleonisp.singlesave.service.SuggestionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/save-points")
public class SavePointController {

    private final SavePointService savePointService;
    private final SuggestionService suggestionService;

    public SavePointController(SavePointService savePointService,
                               SuggestionService suggestionService) {
        this.savePointService = savePointService;
        this.suggestionService = suggestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SavePointResponse> create(OAuth2AuthenticationToken auth,
                                          @RequestBody Mono<CreateSavePointRequest> reqMono) {
        return reqMono.flatMap(req -> savePointService.create(auth, req));
    }

    @GetMapping
    public Flux<SavePointResponse> list(OAuth2AuthenticationToken auth,
                                        @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return savePointService.list(auth, limit);
    }

    @GetMapping("/latest")
    public Mono<SavePointResponse> latest(OAuth2AuthenticationToken auth) {
        return savePointService.latest(auth);
    }

    @GetMapping("/suggestions")
    public Mono<SuggestionResponse> suggest(OAuth2AuthenticationToken auth) {
        return suggestionService.suggestSavePoint(auth);
    }
}
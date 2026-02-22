package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.SavePointDtos.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import com.aleonisp.singlesave.repository.InMemorySavePointStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/savepoints")
public class SavePointController {

    private final InMemorySavePointStore store;

    public SavePointController(InMemorySavePointStore store) {
        this.store = store;
    }

    @PostMapping
    public Mono<ResponseEntity<SavePointResponse>> create(@RequestBody Mono<CreateSavePointRequest> reqMono) {
        return reqMono
                .map(req -> req == null ? null : req.action())
                .map(action -> action == null ? "" : action.trim())
                .flatMap(action -> {
                    if (action.isBlank()) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    SavePointResponse created = store.add(action);
                    return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(created));
                });
    }

    @GetMapping
    public Flux<SavePointResponse> list() {
        return Flux.fromIterable(store.list());
    }

    @GetMapping("/today")
    public Mono<ResponseEntity<SavePointResponse>> today() {
        SavePointResponse latest = store.latestOrNull();
        if (latest == null) {
            return Mono.just(ResponseEntity.noContent().build());
        }
        return Mono.just(ResponseEntity.ok(latest));
    }
}
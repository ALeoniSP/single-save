package com.aleonisp.singlesave.controller;

import com.aleonisp.singlesave.dto.SavePointDtos.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import com.aleonisp.singlesave.service.SavePointService;
import org.springframework.http.HttpStatus;
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
    public Mono<SavePointResponse> create(@RequestBody Mono<CreateSavePointRequest> reqMono) {
        return reqMono.map(CreateSavePointRequest::action)
                .flatMap(service::create);
    }

    @GetMapping
    public Flux<SavePointResponse> list(@RequestParam(name = "limit", defaultValue = "50") int limit) {
        return service.list(limit);
    }

    @GetMapping("/latest")
    public Mono<SavePointResponse> latest() {
        return service.latest();
    }
}
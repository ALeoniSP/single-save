package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavePointService {
    Mono<SavePointResponse> create(String action);
    Flux<SavePointResponse> list(int limit);
    Mono<SavePointResponse> latest();
}
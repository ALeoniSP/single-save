package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.SavePointResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavePointService {
    Mono<SavePointResponse> create(String provider, String providerSubject, String action);
    Flux<SavePointResponse> list(String provider, String providerSubject, int limit);
    Mono<SavePointResponse> latest(String provider, String providerSubject);
}
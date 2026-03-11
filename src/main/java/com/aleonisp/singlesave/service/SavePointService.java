package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavePointService {

    Mono<SavePointResponse> create(OAuth2AuthenticationToken auth, CreateSavePointRequest request);

    Flux<SavePointResponse> list(OAuth2AuthenticationToken auth, int limit);

    Mono<SavePointResponse> latest(OAuth2AuthenticationToken auth);
}
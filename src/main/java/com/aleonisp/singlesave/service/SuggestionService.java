package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.SuggestionResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

public interface SuggestionService {

    Mono<SuggestionResponse> suggestSavePoint(OAuth2AuthenticationToken auth);
}
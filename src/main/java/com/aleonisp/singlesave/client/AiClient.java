package com.aleonisp.singlesave.client;

import com.aleonisp.singlesave.dto.SuggestionResponse;
import reactor.core.publisher.Mono;

public interface AiClient {

    Mono<SuggestionResponse> suggestSavePoint(String prompt);
}
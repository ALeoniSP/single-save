package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.AccountResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountResponse> getCurrentAccount(OAuth2AuthenticationToken auth);
}
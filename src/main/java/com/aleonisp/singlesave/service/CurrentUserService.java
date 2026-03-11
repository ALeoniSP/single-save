package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.model.CurrentUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

public interface CurrentUserService {

    Mono<CurrentUser> getCurrentUser(OAuth2AuthenticationToken auth);
}
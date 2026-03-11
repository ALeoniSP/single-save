package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.repository.InMemorySavePointStore;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InMemorySavePointService implements SavePointService {

    private static final int MAX_ACTION_LEN = 120;

    private final InMemorySavePointStore store;
    private final CurrentUserService currentUserService;

    public InMemorySavePointService(InMemorySavePointStore store,
                                    CurrentUserService currentUserService) {
        this.store = store;
        this.currentUserService = currentUserService;
    }

    @Override
    public Mono<SavePointResponse> create(OAuth2AuthenticationToken auth, CreateSavePointRequest request) {
        return currentUserService.getCurrentUser(auth)
                .map(currentUser -> normalize(request.action()))
                .map(normalizedAction -> {
                    validateAction(normalizedAction);
                    return normalizedAction;
                })
                .zipWith(currentUserService.getCurrentUser(auth))
                .map(tuple -> store.add(tuple.getT2().id(), tuple.getT1()));
    }

    @Override
    public Flux<SavePointResponse> list(OAuth2AuthenticationToken auth, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));

        return currentUserService.getCurrentUser(auth)
                .flatMapMany(currentUser -> Flux.fromIterable(store.list(currentUser.id()))
                        .take(safeLimit));
    }

    @Override
    public Mono<SavePointResponse> latest(OAuth2AuthenticationToken auth) {
        return currentUserService.getCurrentUser(auth)
                .flatMap(currentUser -> Mono.justOrEmpty(store.latestOrNull(currentUser.id())))
                .switchIfEmpty(Mono.error(new DomainException(
                        "SAVEPOINT_NOT_FOUND",
                        HttpStatus.NOT_FOUND,
                        "No savepoints found"
                )));
    }

    private void validateAction(String action) {
        if (action.isBlank()) {
            throw new DomainException(
                    "VALIDATION_ERROR",
                    HttpStatus.BAD_REQUEST,
                    "action must not be blank"
            );
        }

        if (action.length() > MAX_ACTION_LEN) {
            throw new DomainException(
                    "VALIDATION_ERROR",
                    HttpStatus.BAD_REQUEST,
                    "action too long (max 120)"
            );
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
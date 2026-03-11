package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.CreateSavePointRequest;
import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.model.SavePointEntity;
import com.aleonisp.singlesave.repository.SavePointRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Primary
public class R2dbcSavePointService implements SavePointService {

    private static final int MAX_ACTION_LEN = 120;

    private final SavePointRepository repo;
    private final CurrentUserService currentUserService;

    public R2dbcSavePointService(SavePointRepository repo,
                                 CurrentUserService currentUserService) {
        this.repo = repo;
        this.currentUserService = currentUserService;
    }

    @Override
    public Mono<SavePointResponse> create(OAuth2AuthenticationToken auth, CreateSavePointRequest request) {
        return currentUserService.getCurrentUser(auth)
                .map(currentUser -> {
                    String normalizedAction = normalize(request.action());
                    validateAction(normalizedAction);

                    return new SavePointEntity(
                            null,
                            currentUser.provider(),
                            currentUser.subject(),
                            normalizedAction,
                            null
                    );
                })
                .flatMap(repo::save)
                .map(this::toResponse);
    }

    @Override
    public Flux<SavePointResponse> list(OAuth2AuthenticationToken auth, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));

        return currentUserService.getCurrentUser(auth)
                .flatMapMany(currentUser ->
                        repo.findLatest(currentUser.provider(), currentUser.subject(), safeLimit)
                                .map(this::toResponse)
                );
    }

    @Override
    public Mono<SavePointResponse> latest(OAuth2AuthenticationToken auth) {
        return currentUserService.getCurrentUser(auth)
                .flatMap(currentUser ->
                        repo.findLatestOne(currentUser.provider(), currentUser.subject())
                )
                .switchIfEmpty(Mono.error(new DomainException(
                        "SAVEPOINT_NOT_FOUND",
                        HttpStatus.NOT_FOUND,
                        "No savepoints found"
                )))
                .map(this::toResponse);
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

    private SavePointResponse toResponse(SavePointEntity entity) {
        return new SavePointResponse(
                entity.id(),
                entity.action(),
                entity.createdAt()
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
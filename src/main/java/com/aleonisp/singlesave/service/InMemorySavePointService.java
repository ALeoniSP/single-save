package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.repository.InMemorySavePointStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InMemorySavePointService implements SavePointService {

    private static final int MAX_ACTION_LEN = 120;

    private final InMemorySavePointStore store;

    public InMemorySavePointService(InMemorySavePointStore store) {
        this.store = store;
    }

    @Override
    public Mono<SavePointResponse> create(String action) {
        return Mono.fromSupplier(() -> normalize(action))
                .map(norm -> {
                    if (norm.isBlank()) {
                        throw new DomainException("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "action must not be blank");
                    }
                    if (norm.length() > MAX_ACTION_LEN) {
                        throw new DomainException("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "action too long (max 120)");
                    }
                    return store.add(norm);
                });
    }

    @Override
    public Flux<SavePointResponse> list(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return Flux.fromIterable(store.list())
                .take(safeLimit);
    }

    @Override
    public Mono<SavePointResponse> latest() {
        return Mono.fromSupplier(store::latestOrNull)
                .flatMap(sp -> sp == null
                        ? Mono.error(new DomainException("SAVEPOINT_NOT_FOUND", HttpStatus.NOT_FOUND, "No savepoints found"))
                        : Mono.just(sp));
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim();
    }
}
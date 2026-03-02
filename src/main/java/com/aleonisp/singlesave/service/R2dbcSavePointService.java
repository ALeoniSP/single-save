package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.SavePointResponse;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.model.SavePointEntity;
import com.aleonisp.singlesave.repository.SavePointRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Primary
public class R2dbcSavePointService implements SavePointService {

    private static final int MAX_ACTION_LEN = 120;

    private final SavePointRepository repo;

    public R2dbcSavePointService(SavePointRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<SavePointResponse> create(String provider, String providerSubject, String action) {
        return Mono.fromSupplier(() -> normalize(action))
                .flatMap(norm -> {
                    if (norm.isBlank()) {
                        return Mono.error(new DomainException("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "action must not be blank"));
                    }
                    if (norm.length() > MAX_ACTION_LEN) {
                        return Mono.error(new DomainException("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "action too long (max 120)"));
                    }

                    SavePointEntity toSave = new SavePointEntity(
                            null,
                            provider,
                            providerSubject,
                            norm,
                            null
                    );

                    return repo.save(toSave)
                            .map(this::toResponse);
                });
    }

    @Override
    public Flux<SavePointResponse> list(String provider, String providerSubject, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return repo.findLatest(provider, providerSubject, safeLimit)
                .map(this::toResponse);
    }

    @Override
    public Mono<SavePointResponse> latest(String provider, String providerSubject) {
        return repo.findLatestOne(provider, providerSubject)
                .switchIfEmpty(Mono.error(new DomainException("SAVEPOINT_NOT_FOUND", HttpStatus.NOT_FOUND, "No savepoints found")))
                .map(this::toResponse);
    }

    private SavePointResponse toResponse(SavePointEntity e) {
        return new SavePointResponse(e.id(), e.action(), e.createdAt());
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim();
    }
}
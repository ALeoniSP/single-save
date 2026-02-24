package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.model.SavePointEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SavePointRepository extends ReactiveCrudRepository<SavePointEntity, UUID> {

    @Query("""
        SELECT * FROM save_points
        WHERE provider = :provider AND provider_subject = :providerSubject
        ORDER BY created_at DESC
        LIMIT :limit
        """)
    Flux<SavePointEntity> findLatest(String provider, String providerSubject, int limit);

    @Query("""
        SELECT * FROM save_points
        WHERE provider = :provider AND provider_subject = :providerSubject
        ORDER BY created_at DESC
        LIMIT 1
        """)
    Mono<SavePointEntity> findLatestOne(String provider, String providerSubject);
}
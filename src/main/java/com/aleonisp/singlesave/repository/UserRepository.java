package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.model.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
    Mono<UserEntity> findByProviderAndProviderSubject(String provider, String providerSubject);
}
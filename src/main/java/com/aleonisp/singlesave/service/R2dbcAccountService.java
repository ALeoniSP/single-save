package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.dto.AccountResponse;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.model.UserEntity;
import com.aleonisp.singlesave.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class R2dbcAccountService implements AccountService {

    private final UserRepository userRepository;
    private final TransactionalOperator tx;

    public R2dbcAccountService(UserRepository userRepository, TransactionalOperator tx) {
        this.userRepository = userRepository;
        this.tx = tx;
    }

    @Override
    public Mono<AccountResponse> getCurrentAccount(OAuth2AuthenticationToken auth) {
        return Mono.defer(() -> {
            if (auth == null || !auth.isAuthenticated()) {
                return Mono.error(new DomainException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Not authenticated"));
            }

            if (!(auth.getPrincipal() instanceof OidcUser oidcUser)) {
                return Mono.error(new DomainException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "OIDC principal missing"));
            }

            String provider = auth.getAuthorizedClientRegistrationId();
            String providerSubject = oidcUser.getSubject();
            String email = OidcUserFields.email(oidcUser);
            String displayName = OidcUserFields.displayName(oidcUser);

            Instant now = Instant.now();

            return userRepository.findByProviderAndProviderSubject(provider, providerSubject)
                    .flatMap(existing -> userRepository.save(new UserEntity(
                            existing.id(),
                            provider,
                            providerSubject,
                            email,
                            displayName,
                            existing.createdAt() != null ? existing.createdAt() : now,
                            now
                    )))
                    .switchIfEmpty(userRepository.save(new UserEntity(
                            null,
                            provider,
                            providerSubject,
                            email,
                            displayName,
                            now,
                            now
                    )))
                    .map(this::toResponse)
                    .as(tx::transactional);
        });
    }

    private AccountResponse toResponse(UserEntity e) {
        return new AccountResponse(
                e.id(),
                e.provider(),
                e.providerSubject(),
                e.email(),
                e.displayName(),
                e.createdAt(),
                e.lastLoginAt()
        );
    }
}
package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.model.UserEntity;
import com.aleonisp.singlesave.exception.DomainException;
import com.aleonisp.singlesave.repository.UserRepository;
import com.aleonisp.singlesave.model.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class DefaultCurrentUserService implements CurrentUserService {

    private static final String REASON_CODE_INVALID_AUTHENTICATION = "INVALID_AUTHENTICATION";

    private final UserRepository userRepository;

    public DefaultCurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<CurrentUser> getCurrentUser(OAuth2AuthenticationToken auth) {
        return Mono.defer(() -> {
            String provider = auth.getAuthorizedClientRegistrationId();

            if (!(auth.getPrincipal() instanceof OidcUser oidcUser)) {
                return Mono.error(new DomainException(
                        REASON_CODE_INVALID_AUTHENTICATION,
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated principal is not an OIDC user."
                ));
            }

            String subject = oidcUser.getSubject();
            String email = oidcUser.getEmail();
            String displayName = oidcUser.getFullName();
            Instant now = Instant.now();

            return userRepository.findByProviderAndProviderSubject(provider, subject)
                    .switchIfEmpty(createUser(provider, subject, email, displayName, now))
                    .map(this::toCurrentUser);
        });
    }

    private Mono<UserEntity> createUser(String provider,
                                        String subject,
                                        String email,
                                        String displayName,
                                        Instant now) {
        UserEntity entity = new UserEntity(
                UUID.randomUUID(),
                provider,
                subject,
                email,
                displayName,
                now,
                now
        );

        return userRepository.save(entity);
    }

    private CurrentUser toCurrentUser(UserEntity user) {
        return new CurrentUser(
                user.id(),
                user.provider(),
                user.providerSubject(),
                user.email(),
                user.displayName()
        );
    }
}
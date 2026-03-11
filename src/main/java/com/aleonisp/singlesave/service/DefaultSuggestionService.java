package com.aleonisp.singlesave.service;

import com.aleonisp.singlesave.client.AiClient;
import com.aleonisp.singlesave.dto.SuggestionResponse;
import com.aleonisp.singlesave.model.CurrentUser;
import com.aleonisp.singlesave.model.SavePointEntity;
import com.aleonisp.singlesave.repository.SavePointRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultSuggestionService implements SuggestionService {

    private final CurrentUserService currentUserService;
    private final SavePointRepository savePointRepository;
    private final AiClient aiClient;

    public DefaultSuggestionService(CurrentUserService currentUserService,
                                    SavePointRepository savePointRepository,
                                    AiClient aiClient) {
        this.currentUserService = currentUserService;
        this.savePointRepository = savePointRepository;
        this.aiClient = aiClient;
    }

    @Override
    public Mono<SuggestionResponse> suggestSavePoint(OAuth2AuthenticationToken auth) {
        return currentUserService.getCurrentUser(auth)
                .flatMap(currentUser ->
                        savePointRepository.findLatest(currentUser.provider(), currentUser.subject(), 5)
                                .collectList()
                                .map(recentSavePoints -> buildPrompt(currentUser, recentSavePoints))
                )
                .flatMap(aiClient::suggestSavePoint);
    }

    private String buildPrompt(CurrentUser currentUser, List<SavePointEntity> recentSavePoints) {
        String recentActions = recentSavePoints.isEmpty()
                ? "No previous save points."
                : recentSavePoints.stream()
                .map(SavePointEntity::action)
                .collect(Collectors.joining("; "));

        return """
                Generate one realistic save point suggestion for this authenticated user.

                User:
                - display name: %s
                - email: %s

                Recent save point actions:
                %s

                The suggestion must be:
                - concrete
                - small enough to do today
                - aligned with previous behavior when possible
                - not generic
                - not motivational fluff
                """.formatted(
                safe(currentUser.displayName()),
                safe(currentUser.email()),
                recentActions
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }
}
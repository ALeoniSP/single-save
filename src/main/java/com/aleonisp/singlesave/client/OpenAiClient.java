package com.aleonisp.singlesave.client;

import com.aleonisp.singlesave.config.AiProperties;
import com.aleonisp.singlesave.dto.SuggestionResponse;
import com.aleonisp.singlesave.exception.DomainException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiClient implements AiClient {

    private static final String REASON_CODE_AI_UNAVAILABLE = "AI_UNAVAILABLE";
    private static final String REASON_CODE_AI_INVALID_RESPONSE = "AI_INVALID_RESPONSE";

    private final WebClient webClient;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    public OpenAiClient(WebClient.Builder webClientBuilder,
                        AiProperties aiProperties,
                        ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com")
                .build();
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<SuggestionResponse> suggestSavePoint(String prompt) {
        if (aiProperties.apiKey() == null || aiProperties.apiKey().isBlank()) {
            return Mono.error(new DomainException(
                    REASON_CODE_AI_UNAVAILABLE,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "OpenAI API key is not configured."
            ));
        }

        ChatCompletionRequest request = new ChatCompletionRequest(
                aiProperties.model(),
                List.of(
                        new ChatMessage(
                                "system",
                                """
                                You generate one save point suggestion for the app Single Save.

                                Return only valid JSON with exactly these fields:
                                - title
                                - description
                                - reason

                                Rules:
                                - title must be a short actionable phrase
                                - description must explain the concrete action
                                - reason must explain why this suggestion fits the user
                                - no markdown
                                - no code fences
                                - no extra fields
                                - no motivational fluff
                                """
                        ),
                        new ChatMessage("user", prompt)
                ),
                new ResponseFormat(
                        "json_schema",
                        new JsonSchemaWrapper(
                                "save_point_suggestion",
                                true,
                                Map.of(
                                        "type", "object",
                                        "additionalProperties", false,
                                        "properties", Map.of(
                                                "title", Map.of("type", "string"),
                                                "description", Map.of("type", "string"),
                                                "reason", Map.of("type", "string")
                                        ),
                                        "required", List.of("title", "description", "reason")
                                )
                        )
                )
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .map(this::extractContent)
                .map(this::parseSuggestion);
    }

    private String extractContent(ChatCompletionResponse response) {
        if (response == null
                || response.choices() == null
                || response.choices().isEmpty()
                || response.choices().getFirst().message() == null
                || response.choices().getFirst().message().content() == null
                || response.choices().getFirst().message().content().isBlank()) {
            throw new DomainException(
                    REASON_CODE_AI_INVALID_RESPONSE,
                    HttpStatus.BAD_GATEWAY,
                    "AI returned an empty response."
            );
        }

        return response.choices().getFirst().message().content();
    }

    private SuggestionResponse parseSuggestion(String rawJson) {
        try {
            return objectMapper.readValue(rawJson, SuggestionResponse.class);
        } catch (Exception ex) {
            throw new DomainException(
                    REASON_CODE_AI_INVALID_RESPONSE,
                    HttpStatus.BAD_GATEWAY,
                    "AI returned an invalid suggestion payload."
            );
        }
    }

    private record ChatCompletionRequest(
            String model,
            List<ChatMessage> messages,
            ResponseFormat response_format
    ) {
    }

    private record ChatMessage(
            String role,
            String content
    ) {
    }

    private record ResponseFormat(
            String type,
            JsonSchemaWrapper json_schema
    ) {
    }

    private record JsonSchemaWrapper(
            String name,
            boolean strict,
            Map<String, Object> schema
    ) {
    }

    private record ChatCompletionResponse(
            List<Choice> choices
    ) {
    }

    private record Choice(
            Message message
    ) {
    }

    private record Message(
            String content
    ) {
    }
}
package com.aleonisp.singlesave.dto;

public record SuggestionResponse(
        String title,
        String description,
        String reason
) {
}
package com.aleonisp.singlesave.dto;

import java.time.Instant;

public final class SavePointDtos {
    private SavePointDtos() {}

    public record CreateSavePointRequest(String action) {}

    public record SavePointResponse(
            String id,
            String action,
            Instant createdAt
    ) {}
}
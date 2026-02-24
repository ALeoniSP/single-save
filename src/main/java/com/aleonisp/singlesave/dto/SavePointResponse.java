package com.aleonisp.singlesave.dto;

import java.time.Instant;
import java.util.UUID;

public record SavePointResponse(
        UUID id,
        String action,
        Instant createdAt
) {}
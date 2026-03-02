package com.aleonisp.singlesave.dto;

import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String provider,
        String providerSubject,
        String email,
        String displayName,
        Instant createdAt,
        Instant lastLoginAt
) {}
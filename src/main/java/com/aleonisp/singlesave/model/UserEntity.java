package com.aleonisp.singlesave.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("users")
public record UserEntity(
        @Id UUID id,
        String provider,
        String providerSubject,
        String email,
        String displayName,
        Instant createdAt,
        Instant lastLoginAt
) {}
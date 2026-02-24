package com.aleonisp.singlesave.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("save_points")
public record SavePointEntity(
        @Id UUID id,
        String provider,
        String providerSubject,
        String action,
        Instant createdAt
) {}
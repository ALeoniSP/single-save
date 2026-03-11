package com.aleonisp.singlesave.model;

import java.util.UUID;

public record CurrentUser(
        UUID id,
        String provider,
        String subject,
        String email,
        String displayName
) {
}
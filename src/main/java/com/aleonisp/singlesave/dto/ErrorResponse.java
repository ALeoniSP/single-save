package com.aleonisp.singlesave.dto;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String reasonCode,
        String message,
        String path,
        Instant timestamp
) {}
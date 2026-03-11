package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.dto.SavePointResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySavePointStore {

    private final Map<UUID, List<SavePointResponse>> data = new ConcurrentHashMap<>();

    public SavePointResponse add(UUID userId, String action) {
        List<SavePointResponse> savePoints = data.computeIfAbsent(userId, ignored -> new ArrayList<>());

        SavePointResponse savePoint = new SavePointResponse(
                UUID.randomUUID(),
                action,
                Instant.now()
        );

        savePoints.add(0, savePoint);
        return savePoint;
    }

    public List<SavePointResponse> list(UUID userId) {
        return data.getOrDefault(userId, List.of());
    }

    public SavePointResponse latestOrNull(UUID userId) {
        List<SavePointResponse> savePoints = data.get(userId);
        if (savePoints == null || savePoints.isEmpty()) {
            return null;
        }
        return savePoints.getFirst();
    }
}
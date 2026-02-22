package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemorySavePointStore {
    private final CopyOnWriteArrayList<SavePointResponse> store = new CopyOnWriteArrayList<>();

    public SavePointResponse add(String action) {
        SavePointResponse sp = new SavePointResponse(
                UUID.randomUUID().toString(),
                action,
                Instant.now()
        );
        store.add(0, sp);
        return sp;
    }

    public List<SavePointResponse> list() {
        return List.copyOf(store);
    }

    public SavePointResponse latestOrNull() {
        return store.isEmpty() ? null : store.get(0);
    }
}
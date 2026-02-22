package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.dto.SavePointDtos.SavePointResponse;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemorySavePointStore {

    private final ConcurrentLinkedDeque<SavePointResponse> store = new ConcurrentLinkedDeque<>();

    public SavePointResponse add(String action) {
        SavePointResponse sp = new SavePointResponse(
                UUID.randomUUID().toString(),
                action,
                Instant.now()
        );
        store.addFirst(sp);
        return sp;
    }

    public List<SavePointResponse> list() {
        return List.copyOf(store);
    }

    public SavePointResponse latestOrNull() {
        return store.peekFirst();
    }
}
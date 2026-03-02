package com.aleonisp.singlesave.repository;

import com.aleonisp.singlesave.dto.SavePointResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemorySavePointStore {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SavePointResponse>> storeByUser = new ConcurrentHashMap<>();

    private static String key(String provider, String providerSubject) {
        return provider + "|" + providerSubject;
    }

    public SavePointResponse add(String provider, String providerSubject, String action) {
        SavePointResponse sp = new SavePointResponse(
                UUID.randomUUID(),
                action,
                Instant.now()
        );
        storeByUser.computeIfAbsent(key(provider, providerSubject), k -> new CopyOnWriteArrayList<>())
                .add(0, sp);
        return sp;
    }

    public List<SavePointResponse> list(String provider, String providerSubject) {
        CopyOnWriteArrayList<SavePointResponse> list = storeByUser.get(key(provider, providerSubject));
        return list == null ? List.of() : List.copyOf(list);
    }

    public SavePointResponse latestOrNull(String provider, String providerSubject) {
        CopyOnWriteArrayList<SavePointResponse> list = storeByUser.get(key(provider, providerSubject));
        return (list == null || list.isEmpty()) ? null : list.getFirst();
    }
}
package com.type.multi_typer.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RoomSseManager {
    public final Map<String, List<SseEmitter>> roomEmitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String roomCode) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        roomEmitters
                .computeIfAbsent(roomCode, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        Runnable cleanup = () -> {roomEmitters.remove(roomCode);};
        emitter.onCompletion(() -> removeEmitter(roomCode, emitter));
        emitter.onTimeout(() -> removeEmitter(roomCode, emitter));
        emitter.onError((e) -> removeEmitter(roomCode, emitter));

        return emitter;
    }

    public void sendEvent(String roomCode, Object data) {
        List<SseEmitter> emitters = roomEmitters.get(roomCode);
        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("playerUpdate").data(data));
            } catch (Exception e) {
                System.err.println("Failed to send to an emitter, it will be removed. Error: " + e.getMessage());
            }
        }
    }

    public void removeEmitter(String roomCode, SseEmitter emitter) {
        List<SseEmitter> emitters = roomEmitters.get(roomCode);
        if (emitters == null) return;
        emitters.remove(emitter);
        if (emitters.isEmpty()) roomEmitters.remove(roomCode);
    }
}


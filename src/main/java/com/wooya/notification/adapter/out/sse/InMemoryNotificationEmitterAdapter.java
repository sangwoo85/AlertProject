package com.wooya.notification.adapter.out.sse;

import com.wooya.notification.application.port.out.NotificationEmitterPort;
import com.wooya.notification.domain.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Component
public class InMemoryNotificationEmitterAdapter implements NotificationEmitterPort {

    private final Map<String, Map<String, SseEmitter>> store = new ConcurrentHashMap<>();
    private final Executor notificationTaskExecutor;

    public InMemoryNotificationEmitterAdapter(
            @Qualifier("notificationTaskExecutor") Executor notificationTaskExecutor
    ) {
        this.notificationTaskExecutor = notificationTaskExecutor;
    }

    @Override
    public void add(String topic, String emitterId, SseEmitter emitter) {
        store.computeIfAbsent(topic, key -> new ConcurrentHashMap<>()).put(emitterId, emitter);
    }

    @Override
    public void remove(String topic, String emitterId) {
        Map<String, SseEmitter> emitters = store.get(topic);
        if (emitters == null) return;

        emitters.remove(emitterId);
        if (emitters.isEmpty()) {
            store.remove(topic);
        }
    }

    @Override
    public void sendToTopic(String topic, String eventName, NotificationMessage message) {
        notificationTaskExecutor.execute(() -> {
            Map<String, SseEmitter> emitters = store.get(topic);
            if (emitters == null) return;

            emitters.forEach((id, emitter) -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .id(String.valueOf(Instant.now().toEpochMilli()))
                            .data(message));
                } catch (IOException e) {
                    remove(topic, id);
                }
            });
        });
    }

    @Override
    public void broadcast(String eventName, NotificationMessage message) {
        notificationTaskExecutor.execute(() ->
                store.keySet().forEach(topic -> {
                    NotificationMessage resolved = new NotificationMessage(
                            topic,
                            message.from(),
                            message.message(),
                            message.sentAt()
                    );
                    sendToTopic(topic, eventName, resolved);
                })
        );
    }

    @Override
    public Set<String> topics() {
        return store.keySet();
    }
}

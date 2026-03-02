package com.wooya.notification.application.service;

import com.wooya.notification.application.port.in.NotificationCommandUseCase;
import com.wooya.notification.application.port.in.NotificationSubscriptionUseCase;
import com.wooya.notification.application.port.out.NotificationEmitterPort;
import com.wooya.notification.domain.model.NotificationMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService implements NotificationCommandUseCase, NotificationSubscriptionUseCase {

    private static final int DEFAULT_TIMER_COUNT = 1000;
    private final NotificationEmitterPort emitterPort;

    public NotificationService(NotificationEmitterPort emitterPort) {
        this.emitterPort = emitterPort;
    }

    @Override
    public SseEmitter subscribe(String clientId, String topic) {
        SseEmitter emitter = new SseEmitter(0L);
        String emitterId = clientId + "-" + UUID.randomUUID();

        emitterPort.add(topic, emitterId, emitter);
        emitter.onCompletion(() -> emitterPort.remove(topic, emitterId));
        emitter.onTimeout(() -> emitterPort.remove(topic, emitterId));
        emitter.onError((e) -> emitterPort.remove(topic, emitterId));

        emitterPort.sendToTopic(topic, "connected", NotificationMessage.connected(topic));
        return emitter;
    }

    @Override
    public Set<String> topics() {
        return emitterPort.topics();
    }

    @Override
    public void sendToTopic(String topic, String from, String message) {
        emitterPort.sendToTopic(topic, "message", NotificationMessage.of(topic, from, message));
    }

    @Override
    public void broadcast(String message) {
        emitterPort.broadcast("broadcast", NotificationMessage.of(null, "system", message));
    }

    @Override
    public void broadcastTimer(int intervalMs, int count) {
        int loop = (count <= 0) ? DEFAULT_TIMER_COUNT : count;
        for (int i = 0; i < loop; i++) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            emitterPort.broadcast("broadcast", NotificationMessage.of(null, "system", "순번 [" + i + "]"));
        }
    }
}

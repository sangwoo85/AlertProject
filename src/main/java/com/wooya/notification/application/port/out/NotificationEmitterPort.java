package com.wooya.notification.application.port.out;

import com.wooya.notification.domain.model.NotificationMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

public interface NotificationEmitterPort {

    void add(String topic, String emitterId, SseEmitter emitter);

    void remove(String topic, String emitterId);

    void sendToTopic(String topic, String eventName, NotificationMessage message);

    void broadcast(String eventName, NotificationMessage message);

    Set<String> topics();
}

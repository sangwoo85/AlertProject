package com.wooya.notification.application.port.in;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

public interface NotificationSubscriptionUseCase {

    SseEmitter subscribe(String clientId, String topic);

    Set<String> topics();
}

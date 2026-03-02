package com.wooya.notification.adapter.in.web;

import com.wooya.notification.application.port.in.NotificationSubscriptionUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationSseController {

    private final NotificationSubscriptionUseCase subscriptionUseCase;

    public NotificationSseController(NotificationSubscriptionUseCase subscriptionUseCase) {
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @GetMapping(value = "/notification/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestParam String clientId,
            @RequestParam String topic
    ) {
        return subscriptionUseCase.subscribe(clientId, topic);
    }
}
